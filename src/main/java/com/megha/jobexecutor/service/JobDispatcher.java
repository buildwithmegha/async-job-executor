package com.megha.jobexecutor.service;

import com.megha.jobexecutor.entity.JobEntity;
import com.megha.jobexecutor.entity.JobStatus;
import com.megha.jobexecutor.repository.JobRepository;
import com.megha.jobexecutor.worker.JobTask;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class JobDispatcher {

    private final BlockingQueue<UUID> jobQueue;
    private final ThreadPoolExecutor jobExecutor;
    private final JobRepository jobRepository;
    private final JobTracker jobTracker;

    public JobDispatcher(BlockingQueue<UUID> jobQueue,
                         ThreadPoolExecutor jobExecutor,
                         JobRepository jobRepository,
                         JobTracker jobTracker) {
        this.jobQueue = jobQueue;
        this.jobExecutor = jobExecutor;
        this.jobRepository = jobRepository;
        this.jobTracker = jobTracker;
    }

    @PostConstruct
    public void startDispatcher() {

        Thread dispatcherThread = new Thread(() -> {
            while (true) {
                try {
                    UUID jobId = jobQueue.take();

                    // submit job with retry wrapper
                    Future<?> future = jobExecutor.submit(() -> executeWithRetry(jobId));

                    // store future for cancel
                    jobTracker.put(jobId, future);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        dispatcherThread.setName("job-dispatcher-thread");
        dispatcherThread.setDaemon(true);
        dispatcherThread.start();
    }

    private void executeWithRetry(UUID jobId) {

        int maxRetries = 3;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {

            try {
                // execute actual task
                new JobTask(jobId, jobRepository).call();
                return; // if success, stop retry loop

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;

            } catch (Exception ex) {

                // reload job
                JobEntity job = jobRepository.findById(jobId)
                        .orElse(null);

                if (job == null) return;

                // if cancelled do not retry
                if (job.getStatus() == JobStatus.CANCELLED) return;

                // if last attempt -> FAILED
                if (attempt == maxRetries) {
                    job.setStatus(JobStatus.FAILED);
                    job.setErrorMessage(ex.getMessage());
                    job.setCompletedAt(LocalDateTime.now());
                    jobRepository.save(job);
                    return;
                }

                // RETRYING
                job.setStatus(JobStatus.RETRYING);
                job.setRetryCount(attempt + 1);
                job.setErrorMessage("Retry attempt: " + (attempt + 1) + " due to: " + ex.getMessage());
                jobRepository.save(job);

                // exponential backoff: 1s, 2s, 4s
                long backoffMillis = (long) Math.pow(2, attempt) * 1000;

                try {
                    Thread.sleep(backoffMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}

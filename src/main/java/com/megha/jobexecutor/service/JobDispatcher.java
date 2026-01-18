package com.megha.jobexecutor.service;

import com.megha.jobexecutor.repository.JobRepository;
import com.megha.jobexecutor.worker.JobTask;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class JobDispatcher {

    private final BlockingQueue<UUID> jobQueue;
    private final ThreadPoolExecutor jobExecutor;
    private final JobRepository jobRepository;

    public JobDispatcher(BlockingQueue<UUID> jobQueue,
                         ThreadPoolExecutor jobExecutor,
                         JobRepository jobRepository) {
        this.jobQueue = jobQueue;
        this.jobExecutor = jobExecutor;
        this.jobRepository = jobRepository;
    }

    @PostConstruct
    public void startDispatcher() {

        Thread dispatcherThread = new Thread(() -> {
            while (true) {
                try {
                    // Wait until a new jobId comes
                    UUID jobId = jobQueue.take();

                    // Submit the job to thread pool
                    jobExecutor.submit(new JobTask(jobId, jobRepository));

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
}

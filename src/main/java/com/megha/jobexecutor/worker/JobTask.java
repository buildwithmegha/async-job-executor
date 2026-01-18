package com.megha.jobexecutor.worker;

import com.megha.jobexecutor.entity.JobEntity;
import com.megha.jobexecutor.entity.JobStatus;
import com.megha.jobexecutor.repository.JobRepository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.Callable;

public class JobTask implements Callable<String> {

    private final UUID jobId;
    private final JobRepository jobRepository;

    public JobTask(UUID jobId, JobRepository jobRepository) {
        this.jobId = jobId;
        this.jobRepository = jobRepository;
    }

    @Override
    public String call() throws Exception {

        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        // If job already cancelled, don't run
        if (job.getStatus() == JobStatus.CANCELLED) {
            return "CANCELLED";
        }

        // RUNNING
        job.setStatus(JobStatus.RUNNING);
        job.setStartedAt(LocalDateTime.now());
        job.setProgress(10);
        jobRepository.save(job);

        try {
            // simulate work
            for (int i = 1; i <= 5; i++) {

                // ✅ if cancellation happens, thread gets interrupted
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Job interrupted");
                }

                Thread.sleep(1000);

                job.setProgress(10 + i * 15);
                jobRepository.save(job);

                // ✅ force failure for testing retry
                if ("FAIL_TEST".equalsIgnoreCase(job.getJobType()) && i == 2) {
                    throw new RuntimeException("Simulated failure for retry demo");
                }
            }

            // SUCCESS
            job.setStatus(JobStatus.SUCCESS);
            job.setProgress(100);
            job.setResultMessage("Job completed successfully");
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);

            return "SUCCESS";

        } catch (InterruptedException e) {
            // CANCELLED
            job.setStatus(JobStatus.CANCELLED);
            job.setErrorMessage("Job cancelled by user");
            job.setCompletedAt(LocalDateTime.now());
            jobRepository.save(job);

            Thread.currentThread().interrupt(); // restore interrupted status
            return "CANCELLED";
        }
    }
}

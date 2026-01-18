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

        // 1) Fetch job from DB
        JobEntity job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        // 2) Mark job as RUNNING
        job.setStatus(JobStatus.RUNNING);
        job.setStartedAt(LocalDateTime.now());
        job.setProgress(10);
        jobRepository.save(job);

        // 3) Simulate work (like heavy processing)
        for (int i = 1; i <= 5; i++) {
            Thread.sleep(1000); // 1 sec delay
            job.setProgress(10 + i * 15); // progress updates
            jobRepository.save(job);
        }

        // 4) Mark SUCCESS
        job.setStatus(JobStatus.SUCCESS);
        job.setProgress(100);
        job.setResultMessage("Job completed successfully");
        job.setCompletedAt(LocalDateTime.now());
        jobRepository.save(job);

        return "SUCCESS";
    }
}

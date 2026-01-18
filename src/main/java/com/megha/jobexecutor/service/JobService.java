package com.megha.jobexecutor.service;

import com.megha.jobexecutor.dto.JobRequest;
import com.megha.jobexecutor.dto.JobResponse;
import com.megha.jobexecutor.entity.JobEntity;
import com.megha.jobexecutor.entity.JobStatus;
import com.megha.jobexecutor.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;

    public JobResponse createJob(JobRequest request) {
        JobEntity job = JobEntity.builder()
                .jobType(request.jobType())
                .payload(request.payload())
                .status(JobStatus.QUEUED)
                .progress(0)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        jobRepository.save(job);
        return toResponse(job);
    }
    private JobResponse toResponse(JobEntity job) {
        return new JobResponse(
                job.getId(),
                job.getJobType(),
                job.getPayload(),
                job.getStatus(),
                job.getProgress(),
                job.getRetryCount(),
                job.getResultMessage(),
                job.getErrorMessage(),
                job.getCreatedAt(),
                job.getStartedAt(),
                job.getCompletedAt()
        );
    }
    public JobResponse getJob(UUID id) {
        JobEntity job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));

        return toResponse(job);
    }

    public List<JobResponse> listJobs() {
        return jobRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }
}

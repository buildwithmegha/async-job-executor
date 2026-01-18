package com.megha.jobexecutor.dto;

import com.megha.jobexecutor.entity.JobStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record JobResponse(
        UUID id,
        String jobType,
        String payload,
        JobStatus status,
        Integer progress,
        Integer retryCount,
        String resultMessage,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {}

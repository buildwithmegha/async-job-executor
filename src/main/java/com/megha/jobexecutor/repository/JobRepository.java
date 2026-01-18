package com.megha.jobexecutor.repository;

import com.megha.jobexecutor.entity.JobEntity;
import com.megha.jobexecutor.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<JobEntity, UUID> {
    List<JobEntity> findByStatus(JobStatus status);
}

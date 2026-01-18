package com.megha.jobexecutor.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String jobType;

    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(nullable = false)
    private Integer progress; // 0..100

    private String resultMessage;

    private String errorMessage;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        if (this.progress == null) {
            this.progress = 0;
        }
        if (this.status == null) {
            this.status = JobStatus.QUEUED;
        }
    }

}

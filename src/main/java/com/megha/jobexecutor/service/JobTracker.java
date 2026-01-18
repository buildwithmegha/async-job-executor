package com.megha.jobexecutor.service;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Component
public class JobTracker {

    private final ConcurrentHashMap<UUID, Future<?>> runningJobs = new ConcurrentHashMap<>();

    public void put(UUID jobId, Future<?> future) {
        runningJobs.put(jobId, future);
    }

    public Future<?> get(UUID jobId) {
        return runningJobs.get(jobId);
    }

    public void remove(UUID jobId) {
        runningJobs.remove(jobId);
    }
}


package com.megha.jobexecutor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
public class MetricsController {

    private final ThreadPoolExecutor jobExecutor;

    public MetricsController(ThreadPoolExecutor jobExecutor) {
        this.jobExecutor = jobExecutor;
    }

    @GetMapping("/metrics/executor")
    public Map<String, Object> executorMetrics() {
        return Map.of(
                "poolSize", jobExecutor.getPoolSize(),
                "activeCount", jobExecutor.getActiveCount(),
                "corePoolSize", jobExecutor.getCorePoolSize(),
                "maxPoolSize", jobExecutor.getMaximumPoolSize(),
                "completedTaskCount", jobExecutor.getCompletedTaskCount(),
                "queueSize", jobExecutor.getQueue().size()
        );
    }
}

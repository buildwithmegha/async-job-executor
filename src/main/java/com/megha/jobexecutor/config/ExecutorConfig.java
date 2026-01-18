package com.megha.jobexecutor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Bean
    public ThreadPoolExecutor JobExecutor(){
        int corePoolSize = 2;
        int maxPoolSize = 4;
        int keepAliveSeconds = 30;

        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(50);
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}

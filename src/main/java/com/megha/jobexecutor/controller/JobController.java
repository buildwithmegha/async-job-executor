package com.megha.jobexecutor.controller;

import com.megha.jobexecutor.dto.JobRequest;
import com.megha.jobexecutor.dto.JobResponse;
import com.megha.jobexecutor.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse createJob(@RequestBody @Valid JobRequest request){
        return jobService.createJob(request);
    }

    @GetMapping("/{id}")
    public JobResponse getJob(@PathVariable UUID id){
        return jobService.getJob(id);// Set the HTTP status code to 201 Created)// Inject the JobService
    }

    @GetMapping
    public List<JobResponse> listJobs() {
        return jobService.listJobs();
    }
    @PostMapping("/{id}/cancel")
    public JobResponse cancelJob(@PathVariable UUID id) {
        return jobService.cancelJob(id);
    }
}


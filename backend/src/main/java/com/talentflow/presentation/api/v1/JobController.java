package com.talentflow.presentation.api.v1;

import com.talentflow.application.recruitment.JobService;
import com.talentflow.presentation.dto.ApiResponse;
import com.talentflow.presentation.dto.request.*;
import com.talentflow.presentation.dto.response.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) { this.jobService = jobService; }

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> create(@Valid @RequestBody CreateJobRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(jobService.createJob(req)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<JobResponse>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        JobFilterParams f = new JobFilterParams(status, null, search, type);
        Page<JobResponse> page = jobService.listJobs(f, pageable);
        return ResponseEntity.ok(ApiResponse.ok(page.getContent(), ApiResponse.Meta.of(page)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getJob(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> update(@PathVariable UUID id, @Valid @RequestBody UpdateJobRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.updateJob(id, req)));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<JobResponse>> publish(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.publish(id)));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
        jobService.close(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        jobService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/{jobId}/pipeline")
    public ResponseEntity<ApiResponse<List<PipelineStageResponse>>> pipeline(@PathVariable UUID jobId) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getPipeline(jobId)));
    }

    @PostMapping("/{jobId}/pipeline")
    public ResponseEntity<ApiResponse<PipelineStageResponse>> createStage(@PathVariable UUID jobId, @Valid @RequestBody CreatePipelineStageRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(jobService.createStage(jobId, req)));
    }

    @PutMapping("/{jobId}/pipeline/reorder")
    public ResponseEntity<ApiResponse<Void>> reorder(@PathVariable UUID jobId, @Valid @RequestBody ReorderPipelineRequest req) {
        jobService.reorderPipeline(jobId, req);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard() {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getDashboard()));
    }
}

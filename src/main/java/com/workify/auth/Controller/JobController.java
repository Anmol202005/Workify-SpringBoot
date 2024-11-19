package com.workify.auth.Controller;

import com.workify.auth.models.Job;
import com.workify.auth.models.JobApplication;
import com.workify.auth.models.dto.JobDto;
import com.workify.auth.models.dto.JobResponseDto;
import com.workify.auth.models.dto.ResponseMessage;
import com.workify.auth.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/post")
    public ResponseEntity<ResponseMessage> postJob(@RequestBody JobDto jobDto, HttpServletRequest request) {
        jobService.postJob(jobDto, request);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Job Posted Successfully")
                .build());
    }

    @GetMapping("/filter/title")
    public ResponseEntity<List<Job>> getJobsByTitle(@RequestParam String title) {
        return ResponseEntity.ok(jobService.getJobsByTitle(title));
    }

    @GetMapping("/filter/location")
    public ResponseEntity<List<Job>> getJobsByLocation(@RequestParam String location) {
        return ResponseEntity.ok(jobService.getJobsByLocation(location));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<JobResponseDto>> filterJobs(@RequestParam(required = false) String title,
                                                           @RequestParam(required = false) String location,
                                                           @RequestParam(required = false) Integer experience,
                                                           @RequestParam(required = false) Integer minSalary,
                                                           @RequestParam(required = false) Integer maxSalary,
                                                           @RequestParam(required = false) String employmentType,
                                                           @RequestParam(required = false) List<String> requiredSkills){
        return ResponseEntity.ok(jobService.filterJobs(title, location, minSalary, maxSalary, experience, employmentType, requiredSkills));
    }
    @PostMapping("apply/applications/{jobId}")
    public ResponseEntity<ResponseMessage> getJob(@PathVariable long jobId,HttpServletRequest request) {
        jobService.apply(jobId,request);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Applied successfully")
                .build());
    }
    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(@RequestParam String search) {

        return ResponseEntity.ok(jobService.searchJobs(search));
    }
    @GetMapping("/recruiter")
    public ResponseEntity<List<Job>> getJobsByRecruiter(HttpServletRequest request) {
        return ResponseEntity.ok(jobService.jobsByRecruiter(request));
    }

    @GetMapping("/applications/candidate") //(applications applied by candidate)
    public ResponseEntity<List<JobApplication>> getApplicationsByCandidate(HttpServletRequest request) {
        return ResponseEntity.ok(jobService.applicationByCandidate(request));
    }
    @GetMapping("/applications/{jobId}")
    public ResponseEntity<List<JobApplication>> getApplicationsForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobService.applicationsForJob(jobId));
    }

}
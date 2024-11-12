package com.workify.auth.Controller;

import com.workify.auth.models.Job;
import com.workify.auth.models.Recruiter;
import com.workify.auth.models.dto.JobDto;
import com.workify.auth.models.dto.RecruiterDto;
import com.workify.auth.service.RecruiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterController {
    private final RecruiterService recruiterService;

    @Autowired
    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRecruiter(@RequestBody RecruiterDto recruiterdto, HttpServletRequest request) {
        return ResponseEntity.ok(recruiterService.createRecruiter(recruiterdto, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecruiterProfile(@PathVariable Integer id) {
        return ResponseEntity.ok(recruiterService.getRecruiterProfile(id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateRecruiterProfile(@RequestBody Recruiter recruiter) {
        return ResponseEntity.ok(recruiterService.updateRecruiterProfile(recruiter));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRecruiterProfile(@PathVariable Integer id) {
        recruiterService.deleteRecruiterProfile(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Recruiter>> getAllRecruiters(Pageable pageable) {
        return ResponseEntity.ok(recruiterService.getAllRecruiters(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Recruiter>> searchRecruiters(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(recruiterService.searchRecruiters(keyword, pageable));
    }

    @GetMapping("/search/companyName")
    public ResponseEntity<Page<Recruiter>> searchByCompanyName(@RequestParam String companyName, Pageable pageable) {
        return ResponseEntity.ok(recruiterService.searchByCompanyName(companyName, pageable));
    }

    @GetMapping("/search/jobTitle")
    public ResponseEntity<Page<Recruiter>> searchByJobTitle(@RequestParam String jobTitle, Pageable pageable) {
        return ResponseEntity.ok(recruiterService.searchByJobTitle(jobTitle, pageable));
    }

    @GetMapping("/search/industry")
    public ResponseEntity<Page<Recruiter>> searchByIndustry(@RequestParam String industry, Pageable pageable) {
        return ResponseEntity.ok(recruiterService.searchByIndustry(industry, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRecruiterByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(recruiterService.getRecruiterByUserId(userId));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateRecruiterFields(@PathVariable Integer id, @RequestBody RecruiterDto recruiterDto) {
        return ResponseEntity.ok(recruiterService.updateRecruiterFields(id, recruiterDto));
    }
    @PostMapping("/{recruiterId}/postJob")
    public ResponseEntity<Job> postJob(@PathVariable Integer recruiterId, @RequestBody JobDto jobDto) {
        return ResponseEntity.ok(recruiterService.postJob(recruiterId, jobDto));
    }
}
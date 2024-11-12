package com.workify.auth.Controller;
import com.workify.auth.models.Recruiter;
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
        return ResponseEntity.ok(recruiterService.createRecruiter(recruiterdto,request));
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRecruiterByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(recruiterService.getRecruiterByUserId(userId));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<?> updateRecruiterFields(@PathVariable Integer id, @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(recruiterService.updateRecruiterFields(id, updates));
    }
}
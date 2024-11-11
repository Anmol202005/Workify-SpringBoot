package com.workify.auth.Controller;

import com.workify.auth.models.Recruiter;
import com.workify.auth.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recruiter")
public class RecruiterController {
    private final RecruiterService recruiterService;

    @Autowired
    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRecruiter(@RequestBody Recruiter recruiter) {
        return ResponseEntity.ok(recruiterService.createRecruiter(recruiter));
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
}
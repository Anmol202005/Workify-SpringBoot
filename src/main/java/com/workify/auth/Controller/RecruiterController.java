package com.workify.auth.Controller;

import com.workify.auth.models.Recruiter;
import com.workify.auth.models.dto.GetResponse;
import com.workify.auth.models.dto.GetResponseRecruiter;
import com.workify.auth.models.dto.ResponseMessage;
import com.workify.auth.models.dto.RecruiterDto;
import com.workify.auth.service.RecruiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
        Recruiter savedRecruiter = recruiterService.createRecruiter(recruiterdto, request);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Recruiter created successfully")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecruiterProfile(@PathVariable Long id) {
        return ResponseEntity.ok(recruiterService.getRecruiterProfile(id));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateRecruiterProfile(@RequestBody RecruiterDto recruiter,HttpServletRequest request) {
        Recruiter savedRecruiter = recruiterService.updateRecruiterProfile(recruiter,request);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Profile Updated successfully")
                .build());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRecruiterProfile(HttpServletRequest request) {
        recruiterService.deleteRecruiterProfile(request);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Recruiter Deleted Successfully")
                .build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<RecruiterDto>> getAllRecruiters(Pageable pageable) {
        List<RecruiterDto> recruiters = recruiterService.getAllRecruitersDto(pageable);
        return ResponseEntity.ok(recruiters);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Recruiter>> searchRecruiters(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(recruiterService.searchRecruiters(keyword, pageable));
    }

    @GetMapping("/search/companyName")
    public ResponseEntity<Page<Recruiter>> searchByCompanyName(@RequestParam String companyName, Pageable pageable) {
        return ResponseEntity.ok(recruiterService.searchByCompanyName(companyName, pageable));
    }
    @GetMapping("/current-recruiter")
    public ResponseEntity<GetResponseRecruiter> getCurrentRecruiter() {
        return ResponseEntity.ok(recruiterService.getCurrentRecruiter());
    }
    @GetMapping("/search/jobTitle")
    public ResponseEntity<Page<Recruiter>> searchByJobTitle(@RequestParam String jobTitle, Pageable pageable) {
        return ResponseEntity.ok(recruiterService.searchByJobTitle(jobTitle, pageable));
    }

    @GetMapping("/search/industry")
    public ResponseEntity<Page<Recruiter>> searchByIndustry(@RequestParam String industry, Pageable pageable) {
        return ResponseEntity.ok(recruiterService.searchByIndustry(industry, pageable));
    }
    @PostMapping("/Profile-picture")
    public ResponseEntity<ResponseMessage> uploadProfilePicture(
            @RequestParam("image") MultipartFile image,
            HttpServletRequest request
    ) throws Exception {


        recruiterService.saveProfilePicture(image,request);

        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Profile photo uploaded successfully")
                .build());
    }
}
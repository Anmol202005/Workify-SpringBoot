package com.workify.auth.Controller;
import com.workify.auth.models.Candidate;
import com.workify.auth.models.Education;
import com.workify.auth.models.ResponseMessage;
import com.workify.auth.models.dto.CandidateDTO;
import com.workify.auth.models.dto.GetResponse;
import com.workify.auth.service.AuthService;
import com.workify.auth.service.CandidateService;
import com.workify.auth.service.CandidateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;


    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createCandidate(
            @RequestBody CandidateDTO candidateDTO,
            HttpServletRequest request) throws IOException {


        Candidate savedCandidate = candidateService.saveCandidateWithEducation(candidateDTO, request);

        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Candidate created successfully")
                .build());
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<GetResponse>> getAllCandidates() {
        List<GetResponse> candidates = candidateService.getAllCandidates();
        return ResponseEntity.ok(candidates);
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<GetResponse> getCandidateById(@PathVariable Long id) {
        GetResponse candidate = candidateService.getCandidateById(id) ;
        return ResponseEntity.ok(candidate);
    }



    @PatchMapping ("/update")
    public ResponseEntity<ResponseMessage> updateCandidate( @RequestBody CandidateDTO candidateDTO, HttpServletRequest request) {
        Candidate updatedCandidate = candidateService.updateCandidate( candidateDTO,request);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("candidate updated successfully")
                .build());
    }


    @DeleteMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteCandidate(HttpServletRequest request) {
        candidateService.deleteCandidate(request);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Candidate deleted successfully").build());
    }

    @PostMapping("/certificate")
    public ResponseEntity<ResponseMessage> uploadCertificate(
            @RequestParam("certificateName") String certificateName,
            @RequestParam("certificateData") MultipartFile certificateFile,
            HttpServletRequest request
            ) throws IOException {


        candidateService.saveCertificate(certificateName,certificateFile,request);

        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Certificate uploaded successfully")
                .build());
    }
    @PostMapping("/resume")
    public ResponseEntity<ResponseMessage> uploadResume(
            @RequestParam("Resume") MultipartFile resume,
            HttpServletRequest request
    ) throws IOException {


        candidateService.saveResume(resume,request);

        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Resume uploaded successfully")
                .build());
    }
    @DeleteMapping("/delete-certificate/{certificateName}")
    public ResponseEntity<ResponseMessage> deleteCertificate(@PathVariable String certificateName,HttpServletRequest request) {
        candidateService.deleteCertificate(certificateName,request);

        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Certificate Deleted successfully")
                .build());
    }
    @DeleteMapping("/delete-resume")
    public ResponseEntity<ResponseMessage> deleteResume(HttpServletRequest request) {
        candidateService.deleteResume(request);

        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Resume Deleted successfully")
                .build());
    }
    @PostMapping("/Profile-picture")
    public ResponseEntity<ResponseMessage> uploadProfilePicture(
            @RequestParam("image") MultipartFile image,
            HttpServletRequest request
    ) throws IOException {


        candidateService.saveProfilePicture(image,request);

        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Profile photo uploaded successfully")
                .build());
    }
    @DeleteMapping("/certificate/{id}")
    public ResponseEntity<ResponseMessage> deleteCertificateById(@PathVariable Long id) {
        candidateService.deleteCertificateById(id);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Certificate deleted successfully")
                .build());
    }
    @DeleteMapping("/delete-certificates-all")
    public ResponseEntity<ResponseMessage> deleteAllCertificates(HttpServletRequest request) {

        candidateService.deleteAllCertificatesByCandidate(request);

        return ResponseEntity.ok(ResponseMessage.builder()
                .message("All certificates deleted successfully")
                .build());
    }


}


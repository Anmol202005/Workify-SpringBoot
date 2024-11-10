package com.workify.auth.Controller;
import com.workify.auth.models.Candidate;
import com.workify.auth.models.ResponseMessage;
import com.workify.auth.service.AuthService;
import com.workify.auth.service.CandidateService;
import com.workify.auth.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {


    private CandidateService candidateService;


    @PostMapping("/create")
    public ResponseEntity<Candidate> createCandidate(@RequestBody Candidate candidate) {
        Candidate createdCandidate = candidateService.createCandidate(candidate);
        return ResponseEntity.ok(createdCandidate);
    }


    @GetMapping("/get-all")
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        return ResponseEntity.ok(candidates);
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<Candidate> getCandidateById(@PathVariable Integer id) {
        Candidate candidate = candidateService.getCandidateById(id) ;
        return ResponseEntity.ok(candidate);
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable Integer id, @RequestBody Candidate candidateDetails) {
        Candidate updatedCandidate = candidateService.updateCandidate(id, candidateDetails);
        return ResponseEntity.ok(updatedCandidate);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteCandidate(@PathVariable Integer id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("Candidate deleted successfully").build());
    }
}


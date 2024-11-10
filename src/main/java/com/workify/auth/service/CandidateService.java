package com.workify.auth.service;



import com.workify.auth.models.Candidate;
import com.workify.auth.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;


    public Candidate createCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }


    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }


    public Candidate getCandidateById(Integer id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id " + id));
    }


    public Candidate updateCandidate(Integer id, Candidate candidateDetails) {
        Candidate candidate = getCandidateById(id);

        if (candidateDetails.getSkills() != null) {
            candidate.setSkills(candidateDetails.getSkills());
        }
        if (candidateDetails.getResume() != null) {
            candidate.setResume(candidateDetails.getResume());
        }
        if (candidateDetails.getExperiences() != null) {
            candidate.setExperiences(candidateDetails.getExperiences());
        }
        if (candidateDetails.getEducationList() != null) {
            candidate.setEducationList(candidateDetails.getEducationList());
        }
        if (candidateDetails.getCertificates() != null) {
            candidate.setCertificates(candidateDetails.getCertificates());
        }

        return candidateRepository.save(candidate);
    }



    public void deleteCandidate(Integer id) {
        Candidate candidate = getCandidateById(id);
        candidateRepository.delete(candidate);
    }
}


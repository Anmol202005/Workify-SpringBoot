package com.workify.auth.service;

import com.workify.auth.models.Recruiter;
import com.workify.auth.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterService {
    private final RecruiterRepository recruiterRepository;

    @Autowired
    public RecruiterService(RecruiterRepository recruiterRepository) {
        this.recruiterRepository = recruiterRepository;
    }

    public Recruiter createRecruiter(Recruiter recruiter) {
        return recruiterRepository.save(recruiter);
    }

    public Optional<Recruiter> getRecruiterProfile(Integer id) {
        return recruiterRepository.findById(id);
    }

    public Recruiter updateRecruiterProfile(Recruiter recruiter) {
        return recruiterRepository.save(recruiter);
    }

    public void deleteRecruiterProfile(Integer id) {
        recruiterRepository.deleteById(id);
    }
}
package com.workify.auth.service;

import com.workify.auth.models.Recruiter;
import com.workify.auth.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
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

    public Page<Recruiter> getAllRecruiters(Pageable pageable) {
        return recruiterRepository.findAll(pageable);
    }

    public Page<Recruiter> searchRecruiters(String keyword, Pageable pageable) {
        return recruiterRepository.findByCompanyNameContainingOrJobTitleContainingOrIndustryContaining(keyword, keyword, keyword, pageable);
    }

    public Optional<Recruiter> getRecruiterByUserId(Integer userId) {
        return recruiterRepository.findByUserId(userId);
    }

    public Recruiter updateRecruiterFields(Integer id, Map<String, Object> updates) {
        Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(id);
        if (optionalRecruiter.isPresent()) {
            Recruiter recruiter = optionalRecruiter.get();
            updates.forEach((key, value) -> {
                switch (key) {
                    case "companyEmail":
                        recruiter.setCompanyEmail((String) value);
                        break;
                    case "companyName":
                        recruiter.setCompanyName((String) value);
                        break;
                    case "jobTitle":
                        recruiter.setJobTitle((String) value);
                        break;
                    case "companyWebsite":
                        recruiter.setCompanyWebsite((String) value);
                        break;
                    case "companyLocation":
                        recruiter.setCompanyLocation((String) value);
                        break;
                    case "industry":
                        recruiter.setIndustry((String) value);
                        break;
                }
            });
            return recruiterRepository.save(recruiter);
        } else {
            throw new RuntimeException("Recruiter not found");
        }
    }
}
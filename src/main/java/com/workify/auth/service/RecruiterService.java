package com.workify.auth.service;

import com.workify.auth.models.Recruiter;
import com.workify.auth.models.User;
import com.workify.auth.models.dto.RecruiterDto;
import com.workify.auth.repository.RecruiterRepository;
import com.workify.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    private UserRepository userRepository;

    @Autowired
    public RecruiterService(RecruiterRepository recruiterRepository) {
        this.recruiterRepository = recruiterRepository;
    }

    public Recruiter createRecruiter(RecruiterDto recruiterdto, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        return convertDtoToRecruiter(recruiterdto, user);
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

    public Page<Recruiter> searchByCompanyName(String companyName, Pageable pageable) {
        return recruiterRepository.findByCompanyNameContaining(companyName, pageable);
    }

    public Page<Recruiter> searchByJobTitle(String jobTitle, Pageable pageable) {
        return recruiterRepository.findByJobTitleContaining(jobTitle, pageable);
    }

    public Page<Recruiter> searchByIndustry(String industry, Pageable pageable) {
        return recruiterRepository.findByIndustryContaining(industry, pageable);
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

    private Recruiter convertDtoToRecruiter(RecruiterDto recruiterDto, Optional<User> user) {
        Recruiter recruiter = new Recruiter();
        recruiter.setUser(user.get());
        recruiter.setCompanyEmail(recruiterDto.getCompanyEmail());
        recruiter.setCompanyName(recruiterDto.getCompanyName());
        recruiter.setJobTitle(recruiterDto.getJobTitle());
        recruiter.setCompanyWebsite(recruiterDto.getCompanyWebsite());
        recruiter.setCompanyLocation(recruiterDto.getCompanyLocation());
        recruiter.setIndustry(recruiterDto.getIndustry());
        recruiterRepository.save(recruiter);
        return recruiter;
    }
}
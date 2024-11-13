package com.workify.auth.service;

import com.workify.auth.models.Job;
import com.workify.auth.models.Recruiter;
import com.workify.auth.models.Role;
import com.workify.auth.models.User;
import com.workify.auth.models.dto.JobDto;
import com.workify.auth.models.dto.RecruiterDto;
import com.workify.auth.repository.JobRepository;
import com.workify.auth.repository.RecruiterRepository;
import com.workify.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        if (user.isPresent()) {
            Recruiter recruiter = convertDtoToRecruiter(recruiterdto, user);
            user.get().setRole(Role.RECRUITER); // Update role to recruiter
            userRepository.save(user.get()); // Save the updated user
            return recruiter;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Optional<Recruiter> getRecruiterProfile(Long id) {
        return recruiterRepository.findById(id);
    }

    public Recruiter updateRecruiterProfile(RecruiterDto recruiter,HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        Optional<Recruiter> recruiterOptional = recruiterRepository.findByUser(user.get());
        if (recruiterOptional.isPresent()) {
            return updateRecruiterFields(recruiterOptional.get().getId(), recruiter);
        } else {
            throw new RuntimeException("Recruiter not found");
        }
    }

    public void deleteRecruiterProfile(HttpServletRequest id) {
        final String authHeader = id.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        Optional<Recruiter> recruiter = recruiterRepository.findByUser(user.get());
        if (recruiter.isPresent()) {
            recruiterRepository.delete(recruiter.get());
        } else {
            throw new RuntimeException("Recruiter not found");
        }
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

    public Optional<Recruiter> getRecruiterByUserId(Long userId) {
        return recruiterRepository.findByUserId(userId);
    }

    public Recruiter updateRecruiterFields(Long id, RecruiterDto recruiterDto) {
        Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(id);
        if (optionalRecruiter.isPresent()) {
            Recruiter recruiter = optionalRecruiter.get();
            recruiter.setCompanyEmail(recruiterDto.getCompanyEmail());
            recruiter.setCompanyName(recruiterDto.getCompanyName());
            recruiter.setJobTitle(recruiterDto.getJobTitle());
            recruiter.setCompanyWebsite(recruiterDto.getCompanyWebsite());
            recruiter.setCompanyLocation(recruiterDto.getCompanyLocation());
            recruiter.setIndustry(recruiterDto.getIndustry());
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
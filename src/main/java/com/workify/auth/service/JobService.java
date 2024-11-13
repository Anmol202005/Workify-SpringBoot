package com.workify.auth.service;

import com.workify.auth.models.Job;
import com.workify.auth.models.Recruiter;
import com.workify.auth.models.User;
import com.workify.auth.models.dto.JobDto;
import com.workify.auth.repository.JobRepository;
import com.workify.auth.repository.RecruiterRepository;
import com.workify.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    private final RecruiterRepository recruiterRepository;
    private final JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public JobService(JobRepository jobRepository, RecruiterRepository recruiterRepository) {
        this.jobRepository = jobRepository;
        this.recruiterRepository = recruiterRepository;
    }

    public Job postJob(JobDto jobDto, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        Optional<Recruiter> recruiterOptional = recruiterRepository.findByUser(user.get());

        if (recruiterOptional.isPresent()) {
            Job job = new Job();
            job.setRecruiter(recruiterOptional.get());
            job.setTitle(jobDto.getTitle());
            job.setDescription(jobDto.getDescription());
            job.setLocation(jobDto.getLocation());
            job.setIndustry(jobDto.getIndustry());
            job.setEmploymentType(jobDto.getEmploymentType());
            return jobRepository.save(job);
        } else {
            throw new RuntimeException("Recruiter not found");
        }
    }
    public List<Job> getJobsByTitle(String title) {
        return jobRepository.findByTitleContaining(title);
    }

    public List<Job> getJobsByLocation(String location) {
        return jobRepository.findByLocationContaining(location);
    }

    public List<Job> getJobsByIndustry(String industry) {
        return jobRepository.findByIndustryContaining(industry);
    }
}
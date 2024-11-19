package com.workify.auth.service;

import com.workify.auth.models.*;
import com.workify.auth.models.dto.JobDto;
import com.workify.auth.models.dto.JobResponseDto;
import com.workify.auth.repository.*;
import jakarta.persistence.criteria.Join;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
//import java.util.function.Predicate;
import jakarta.persistence.criteria.Predicate;

@Service
public class JobService {
    private final RecruiterRepository recruiterRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public JobService(JobRepository jobRepository, RecruiterRepository recruiterRepository, CandidateRepository candidateRepository, JobApplicationRepository jobApplicationRepository) {
        this.jobRepository = jobRepository;
        this.recruiterRepository = recruiterRepository;
        this.candidateRepository = candidateRepository;
        this.jobApplicationRepository = jobApplicationRepository;
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
            job.setPostedBy(recruiterOptional.get());
            job.setTitle(jobDto.getTitle());
            job.setDescription(jobDto.getDescription());
            job.setCompany(recruiterOptional.get().getCompanyName());
            job.setPostedAt(LocalDateTime.now());
            job.setLocation(recruiterOptional.get().getCompanyLocation());
            job.setIndustry(recruiterOptional.get().getIndustry());
            job.setEmploymentType(jobDto.getEmploymentType());
            job.setMaxSalary(jobDto.getMaxSalary());
            job.setMinSalary(jobDto.getMinSalary());
            job.setLocation(jobDto.getLocation());
            job.setRequiredSkills(jobDto.getRequiredSkills());
            job.setExperience(jobDto.getExperience());
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

    public List<JobResponseDto> filterJobs(String title, String location, Integer minSalary, Integer maxSalary, Integer experience, String employmentType, List<String> requiredSkills) {
        Specification<Job> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if(title != null){
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if(location != null){
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(root.get("location"), "%" + location + "%"));
            }
            if(minSalary != null){
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("minSalary"), minSalary));
            }
            if(maxSalary != null){
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("maxSalary"), maxSalary));
            }
            if(experience != null){
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("experience"), experience));
            }
            if(employmentType != null){
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("employmentType"), employmentType));
            }
            if(requiredSkills != null && !requiredSkills.isEmpty()){
                Join<Job, String> skillsJoin = root.join("requiredSkills");
                List<String> lowerCaseSkills = requiredSkills.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lower(skillsJoin).in(lowerCaseSkills));
            }
            return predicate;
        };
        List<Job> filteredJobs = jobRepository.findAll(spec);
        if(filteredJobs.isEmpty()){
            throw new RuntimeException("No Jobs Found");
        }
        else {
            return filteredJobs.stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
        }
    }
    private JobResponseDto mapToResponseDto(Job job) {
        return new JobResponseDto(
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getExperience(),
                job.getMinSalary(),
                job.getMaxSalary(),
                job.getEmploymentType(),
                new ArrayList<>(job.getRequiredSkills())
        );
    }
    public void apply(long jobId, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent() && !user.get().getRole().equals(Role.CANDIDATE)) {
            throw new RuntimeException("Recuiter Can't apply for Jobs");
        }

        Candidate applicant = candidateRepository.findByUser(user);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        JobApplication jobApplication = new JobApplication();
        jobApplication.setJob(job);
        jobApplication.setApplicant(applicant);
        jobApplication.setAppliedAt(LocalDateTime.now());
        jobApplication.setStatus(ApplicationStatus.PENDING);

    }

    public List<Job> searchJobs(String keyword) {
        List<Job> jobs = jobRepository.searchJobs(keyword);
        return jobs;
    }

    public List<Job> jobsByRecruiter(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent() && user.get().getRole().equals(Role.CANDIDATE)) {
            throw new RuntimeException("Invalid User (only recuiters allowed)");
        }
        Optional<Recruiter> recruiterOptional = recruiterRepository.findByUser(user.get());
        Recruiter recruiter = recruiterOptional.get();

            List<Job> jobs = jobRepository.findByPostedById(recruiter.getId());
            return jobs;

    }

    public List<JobApplication> applicationByCandidate(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        if(user.isPresent() && !user.get().getRole().equals(Role.CANDIDATE)) {
            throw new RuntimeException("Invalid User (only candidates allowed)");
        }

        Candidate applicant = candidateRepository.findByUser(user);
        List<JobApplication> applications = jobApplicationRepository.findByApplicantId(applicant.getId());
        return applications;
    }

    public List<JobApplication> applicationsForJob(Long jobId) {
        List<JobApplication> applications = jobApplicationRepository.findByJobId(jobId);
        return applications;
    }
}
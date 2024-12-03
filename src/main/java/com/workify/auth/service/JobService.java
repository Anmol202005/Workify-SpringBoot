package com.workify.auth.service;

import com.workify.auth.models.*;
import com.workify.auth.models.dto.JobDto;
import com.workify.auth.models.dto.JobResponseDto;
import com.workify.auth.models.dto.StatusDto;
import com.workify.auth.repository.*;
import jakarta.persistence.criteria.Join;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
//import java.util.function.Predicate;
import jakarta.persistence.criteria.Predicate;

@Service
public class JobService {
    private final EmailService emailService;
    private final RecruiterRepository recruiterRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    public JobService(EmailService emailService, JobRepository jobRepository, RecruiterRepository recruiterRepository, CandidateRepository candidateRepository, JobApplicationRepository jobApplicationRepository) {
        this.emailService = emailService;
        this.jobRepository = jobRepository;
        this.recruiterRepository = recruiterRepository;
        this.candidateRepository = candidateRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }
    @Transactional
    public void deleteJob(Long jobId) {
        // Delete associated job applications
        jobApplicationRepository.deleteByJobId(jobId);
        // Delete job
        jobRepository.deleteById(jobId);
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
            job.setMaxSalary(jobDto.getMaxSalary());
            job.setMinSalary(jobDto.getMinSalary());
            job.setLocation(jobDto.getLocation());
            job.setExperience(jobDto.getExperience());
            job.setRequiredSkills(jobDto.getRequiredSkills());
            job.setJobType(JobType.valueOf(jobDto.getJobType().toUpperCase()));
            job.setMode(Mode.valueOf(jobDto.getMode().toUpperCase()));
            job.setJobStatus(JobStatus.OPEN);
            List<Candidate> candidates=candidateRepository.findCandidatesBySkills(jobDto.getRequiredSkills());
            for (Candidate candidate : candidates) {
                Notification notification = new Notification();
                notification.setTitle(jobDto.getTitle());
                notification.setMessage(jobDto.getDescription());
                notification.setUser(candidate.getUser());
            }

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

    public List<JobResponseDto> filterJobs(String title, String location, Integer minSalary, Integer maxSalary, Integer experience, List<String> requiredSkills,String jobtypei,String modei,String jobStatusi) {
        final JobType type = (jobtypei != null) ? JobType.valueOf(jobtypei.toUpperCase()) : null;
        final Mode mode = (modei != null) ? Mode.valueOf(modei.toUpperCase()) : null;
        final JobStatus status = (jobStatusi != null) ? JobStatus.valueOf(jobStatusi.toUpperCase()) : null;

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

            if(requiredSkills != null && !requiredSkills.isEmpty()){
                Join<Job, String> skillsJoin = root.join("requiredSkills");
                List<String> lowerCaseSkills = requiredSkills.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lower(skillsJoin).in(lowerCaseSkills));
            }
            if(type != null){
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("jobType"),type));
            }
            if(mode!=null){
                predicate=criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("mode"),mode));
            }
            if(status!=null){
                predicate=criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("jobStatus"),status));
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
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getLocation(),
                job.getExperience(),
                job.getMinSalary(),
                job.getMaxSalary(),
                job.getMode(),
                job.getJobType(),
                job.getJobStatus(),
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
        if(jobApplicationRepository.existsByJobAndApplicant(job, applicant)) {
            throw new RuntimeException("Already applied for this job");
        }
        JobApplication jobApplication = new JobApplication();
        jobApplication.setJob(job);
        jobApplication.setApplicant(applicant);
        jobApplication.setAppliedAt(LocalDateTime.now());
        jobApplication.setStatus(ApplicationStatus.PENDING);
        Recruiter recruiter = job.getPostedBy();
        Notification notification = new Notification();
        notification.setTitle("Applied for your Job");
        String applicantLastName = applicant.getUser().getLastName();
        String lastNamePart = (applicantLastName != null && !applicantLastName.isEmpty()) ? " " + applicantLastName : "";
        notification.setMessage("Applied by "+applicant.getUser().getFirstName()+" "+lastNamePart);

        notification.setUser(recruiter.getUser());
        notificationRepository.save(notification);
        String recruiterEmail = recruiter.getUser().getEmail();
        String applicantFirstName = applicant.getUser().getFirstName();
        String jobTitle = job.getTitle();
        String jobLocation = job.getLocation();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String appliedDate = jobApplication.getAppliedAt().format(dateFormatter);
        String appliedTime = jobApplication.getAppliedAt().format(timeFormatter);

        String subject = "Applied by " + applicantFirstName + lastNamePart +
                " for your job " + jobTitle +
                " at " + jobLocation +
                " on " + appliedDate + " at " + appliedTime;

        sendEmailToRecruiter(recruiterEmail, subject, applicant.getResumeKey(), applicant.getPortfolioKey());
        sendEmailToCandidate(applicant.getUser().getEmail(), applicant.getUser().getFirstName(),applicant.getUser().getLastName(),job.getCompany());
        jobApplicationRepository.save(jobApplication);

    }

    public List<Job> searchJobs(String keyword) {
        return jobRepository.searchJobs(keyword);
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
     //   Page<Job> jobs = jobRepository.findByPostedById(recruiter.getId(), pageable);
        return jobs;
    }


    public List<JobApplication> applicationByCandidate(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && !user.get().getRole().equals(Role.CANDIDATE)) {
            throw new RuntimeException("Invalid User (only candidates allowed)");
        }

        Candidate applicant = candidateRepository.findByUser(user);
        return jobApplicationRepository.findByApplicantId(applicant.getId());
    }

    public List<JobApplication> applicationsForJob(Long jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    public void updateStatus(Long applicationId, StatusDto status) {
        JobApplication jobApplication = jobApplicationRepository.findById(applicationId).get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Optional<Recruiter> recruiter=recruiterRepository.findByUser(currentUser);
        JobApplication jobApplication1 = jobApplicationRepository.findById(applicationId).get();
        Candidate applicant = candidateRepository.findById(jobApplication1.getApplicant().getId()).get();
        if(recruiter.isPresent()) {
            if(jobApplication.getJob().getPostedBy()==recruiter.get()) {

                ApplicationStatus applicationStatus = ApplicationStatus.valueOf(status.getStatus().toUpperCase());
                if(applicationStatus.equals(ApplicationStatus.REJECTED)) {

                    sendRejectedEmailToCandidate(applicant.getUser().getEmail(), applicant.getUser().getFirstName(),applicant.getUser().getLastName(),jobApplication.getJob().getCompany());
                }
                else if(applicationStatus.equals(ApplicationStatus.ACCEPTED)) {
                    Notification notification = new Notification();
                    notification.setTitle("Application Accepted");
                    notification.setMessage("Congratulations! Your resume has been shortlisted. A confirmation email has been sent to you.");
                    notification.setUser(applicant.getUser());
                    notificationRepository.save(notification);
                    sendAcceptedEmailToCandidate(applicant.getUser().getEmail(), applicant.getUser().getFirstName(),applicant.getUser().getLastName(),jobApplication.getJob().getCompany());
                }
                //sendAcceptedEmailToCandidate(applicant.getUser().getEmail(), applicant.getUser().getFirstName(),applicant.getUser().getLastName(),jobApplication.getJob().getCompany());
                jobApplication.setStatus(applicationStatus);


                jobApplicationRepository.save(jobApplication);
            }
            else {
                throw new RuntimeException("Permission denied");
            }
        }

    }
    public void sendEmailToRecruiter(String email, String otp, URL resume, URL portfolio) {
        String subject = "New Applicant";
        String imageUrl = "https://i.ibb.co/kJkpyt6/Workify.png";
        String body = "<html><body>" +
                "<img src='" + imageUrl + "' alt='Verification Image' style='max-width:100%;height:auto;'>" +
                "<p> <strong>" + otp + "</strong></p>" +
                "<p> <strong>Resume: </strong><a href='" + resume + "'>Resume</a></p>" +
                "<p> <strong>Portfolio: </strong><a href='" + portfolio + "'>Portfolio</a></p>" +
                "</body></html>";

        // Set the content type to HTML
        //return emailService.sendEmail(email, subject, body, true);
        if(emailService.sendEmail(email, subject, body, true).getStatusCode().is2xxSuccessful()) {
            System.out.println("Email sent successfully");
        }
        else {
            throw new RuntimeException("Email not sent");
        }

    }
    public void sendEmailToCandidate(String email, String firstname,String lastname,String companyName) {
        String subject = "Thanks for Applying to " + companyName;
        String imageUrl = "https://i.ibb.co/kJkpyt6/Workify.png";
        String body = "<html><body>" +
                "<img src='" + imageUrl + "' alt='Verification Image' style='max-width:100%;height:auto;'>" +
                "<p> Hi "+ firstname +" "+ lastname +", </p>" +
                "<p> Thanks for applying to " + companyName + " There are ton of great companies out there, so we appreciate your interest in joining our team.</p>" +
                "<p> While we're not able to reach out to every applicant, our recruiting team will contact you if your skills and experience are a strong match for the role.</p>" +
                "<p> We appreciate your interest in joining us!</p>" +
                "</body></html>";

        // Set the content type to HTML
        //return emailService.sendEmail(email, subject, body, true);
        if(emailService.sendEmail(email, subject, body, true).getStatusCode().is2xxSuccessful()) {
            System.out.println("Email sent successfully");
        }
        else {
            throw new RuntimeException("Email not sent");
        }

    }
    public void sendAcceptedEmailToCandidate(String email, String firstname,String lastname,String companyName) {
        String subject = "Congratulations For Getting Shortlisted";
        String imageUrl = "https://i.ibb.co/kJkpyt6/Workify.png";
        String body = "<html><body>" +
                "<img src='" + imageUrl + "' alt='Verification Image' style='max-width:100%;height:auto;'>" +
                "<p> Hi "+ firstname +" "+ lastname +", </p>" +
                "<p>Congratulations your resume got shortlisted for further recruitment process our recruiter will contact with you and will share further details with you .</p>" +
                "<p> We appreciate your interest in joining us!</p>" +
                companyName + " <p>Recruiting </p>"+
                "</body></html>";

        // Set the content type to HTML
        //return emailService.sendEmail(email, subject, body, true);
        if(emailService.sendEmail(email, subject, body, true).getStatusCode().is2xxSuccessful()) {
            System.out.println("Email sent successfully");
        }
        else {
            throw new RuntimeException("Email not sent");
        }

    }
    public void sendRejectedEmailToCandidate(String email, String firstname,String lastname,String companyName) {
        String subject = "Dear Applicant";
        String imageUrl = "https://i.ibb.co/kJkpyt6/Workify.png";
        String body = "<html><body>" +
                "<img src='" + imageUrl + "' alt='Verification Image' style='max-width:100%;height:auto;'>" +
                "<p>Thank you for taking the time to apply for the job at "+ companyName + ". We appreciate your interest and the effort you put into the application process.</p>\n" +
                "    <p>After careful consideration, we regret to inform you that we have decided to move forward with other candidates whose qualifications more closely match the requirements of this role.</p>\n" +
                "    <p>Please know that this decision was not an easy one, as we were genuinely impressed with your skills and experience.</p>\n" +
                "    <p>We encourage you to keep an eye on future opportunities with us. We would be delighted to consider your application for other positions that align with your expertise.</p>\n" +
                "    <p>Thank you once again for your interest in joining [Company Name]. We wish you all the best in your job search and future endeavors.</p>" +
                companyName + " <p>Recruiting </p>"+
                "</body></html>";

        // Set the content type to HTML
        //return emailService.sendEmail(email, subject, body, true);
        if(emailService.sendEmail(email, subject, body, true).getStatusCode().is2xxSuccessful()) {
            System.out.println("Email sent successfully");
        }
        else {
            throw new RuntimeException("Email not sent");
        }

    }


    public List<JobResponseDto> getAllJobs() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }
    public Job updateJob(Long jobId, JobDto partialJob) {
        Optional<Job> existingJobOpt = jobRepository.findById(jobId);

        if (existingJobOpt.isPresent()) {
            Job existingJob = existingJobOpt.get();


            if (partialJob.getTitle() != null) existingJob.setTitle(partialJob.getTitle());
            if (partialJob.getDescription() != null) existingJob.setDescription(partialJob.getDescription());
            if (partialJob.getLocation() != null) existingJob.setLocation(partialJob.getLocation());
            if (partialJob.getExperience() != null) existingJob.setExperience(partialJob.getExperience());
            if (partialJob.getJobType() != null) existingJob.setJobType(JobType.valueOf(partialJob.getJobType().toUpperCase()));
            if (partialJob.getMode() != null) existingJob.setMode(Mode.valueOf(partialJob.getMode().toUpperCase()));
            if (partialJob.getMinSalary() != null) existingJob.setMinSalary(partialJob.getMinSalary());
            if (partialJob.getMaxSalary() != null) existingJob.setMaxSalary(partialJob.getMaxSalary());
            if (partialJob.getRequiredSkills() != null) existingJob.setRequiredSkills(partialJob.getRequiredSkills());
            if (partialJob.getJobStatus() != null) existingJob.setJobStatus(JobStatus.valueOf(partialJob.getJobStatus().toUpperCase()));


            return jobRepository.save(existingJob);
        } else {
            throw new RuntimeException("Job with ID " + jobId + " not found.");
        }
    }
    public List<Job> recommendJobs(Candidate candidate, List<Job> jobs) {
        return jobs.stream()
                .sorted(Comparator.comparing((Job job) -> job.getLocation().equalsIgnoreCase(candidate.getLocation()) ? 0 : 1) // Location match first
                        .thenComparing(job -> calculateSkillMatch(candidate.getSkills(), job.getRequiredSkills()), Comparator.reverseOrder()) // Skill match next
                        .thenComparing(Job::getMaxSalary, Comparator.reverseOrder())) // Salary match last
                .collect(Collectors.toList());
    }
    private int calculateSkillMatch(List<String> candidateSkills, List<String> jobSkills) {
        Set<String> uniqueSkills = new HashSet<>(candidateSkills);
        uniqueSkills.retainAll(jobSkills);
        return uniqueSkills.size(); // Return number of matching skills
    }
    public List<Job> recommendedJobsForCandidate(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        var candidate=candidateRepository.findByUser(Optional.ofNullable(currentUser));
        return recommendJobs(candidate,jobRepository.findAll());
    }

}
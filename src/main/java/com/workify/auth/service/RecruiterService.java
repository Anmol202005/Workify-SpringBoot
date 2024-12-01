package com.workify.auth.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.workify.auth.models.Job;
import com.workify.auth.models.Recruiter;
import com.workify.auth.models.Role;
import com.workify.auth.models.User;
import com.workify.auth.models.dto.GetResponse;
import com.workify.auth.models.dto.GetResponseRecruiter;
import com.workify.auth.models.dto.JobDto;
import com.workify.auth.models.dto.RecruiterDto;
import com.workify.auth.repository.JobApplicationRepository;
import com.workify.auth.repository.JobRepository;
import com.workify.auth.repository.RecruiterRepository;
import com.workify.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecruiterService {
    private static final long MAX_PROFILE_PIC_SIZE = 2 * 1024 * 1024; // 2 MB
    private static final long MAX_CERTIFICATE_SIZE = 5 * 1024 * 1024; // 5 MB
    @Value("${aws_bucket_name}")
    private String bucketName;

    private final AmazonS3 amazonS3;
    @Autowired
    private final RecruiterRepository recruiterRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    public RecruiterService(AmazonS3 amazonS3, RecruiterRepository recruiterRepository) {
        this.amazonS3 = amazonS3;
        this.recruiterRepository = recruiterRepository;

    }



    public Recruiter createRecruiter(RecruiterDto recruiterdto, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        if(user.get().getRole()==Role.CANDIDATE){
            throw new RuntimeException("Candidate cannot be a recruiter");
        }
        if (!recruiterRepository.existsByUser(user)) {
            Recruiter recruiter = convertDtoToRecruiter(recruiterdto, user);
            user.get().setRole(Role.RECRUITER);
            userRepository.save(user.get());
            return recruiter;
        } else {
            throw new RuntimeException("Recruiter already exists");
        }
    }

    public Optional<Recruiter> getRecruiterProfile(Long id) {
        return recruiterRepository.findById(id);
    }

    public void updateRecruiterProfile(RecruiterDto recruiter,HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        Optional<Recruiter> recruiterOptional = recruiterRepository.findByUser(user.get());
        if (recruiterOptional.isPresent()) {
             updateRecruiterFields(recruiterOptional.get().getId(), recruiter);
        } else {
            throw new RuntimeException("Recruiter not found");
        }
    }
@Transactional
public List<RecruiterDto> getAllRecruitersDto() {
    List<Recruiter> recruiters = recruiterRepository.findAll();
    return recruiters.stream()
            .map(this::convertRecruiterToDto)
            .collect(Collectors.toList());
}

private RecruiterDto convertRecruiterToDto(Recruiter recruiter) {
    RecruiterDto dto = new RecruiterDto();
    dto.setCompanyEmail(recruiter.getCompanyEmail());
    dto.setCompanyName(recruiter.getCompanyName());
    dto.setJobTitle(recruiter.getJobTitle());
    dto.setCompanyWebsite(recruiter.getCompanyWebsite());
    dto.setCompanyLocation(recruiter.getCompanyLocation());
    dto.setIndustry(recruiter.getIndustry());
    return dto;
}
    @Transactional
    public void deleteRecruiterProfile(HttpServletRequest id) throws Exception {
        final String authHeader = id.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        Optional<Recruiter> recruiter = recruiterRepository.findByUser(user.get());
        if (recruiter.isPresent()) {
            jobRepository.findByPostedBy(recruiter.get()).forEach(job -> {
                jobApplicationRepository.deleteByJobId(((Job)job).getId());
            });
            user.get().setRole(Role.USER);
            if(user.get().getProfileImageKey()!=null) {
                amazonS3.deleteObject(bucketName, getKeyFromUrl(user.get().getProfileImageKey().toString()));

            }
            user.get().setProfileImageKey(null);
            userRepository.save(user.get());
            jobRepository.deleteByPostedBy(recruiter.get());
            recruiterRepository.delete(recruiter.get());
        } else {
            throw new RuntimeException("Recruiter not found");
        }
    }

    public List<Recruiter> getAllRecruiters() {
        return recruiterRepository.findAll();
    }

    public List<Recruiter> searchRecruiters(String keyword) {
        return recruiterRepository.findByCompanyNameContainingOrJobTitleContainingOrIndustryContaining(keyword, keyword, keyword);
    }

    public List<Recruiter> searchByCompanyName(String companyName) {
        return recruiterRepository.findByCompanyNameContaining(companyName);
    }

    public List<Recruiter> searchByJobTitle(String jobTitle) {
        return recruiterRepository.findByJobTitleContaining(jobTitle);
    }

    public List<Recruiter> searchByIndustry(String industry) {
        return recruiterRepository.findByIndustryContaining(industry);
    }

    public Optional<Recruiter> getRecruiterByUserId(Long userId) {
        return recruiterRepository.findByUserId(userId);
    }

    public void updateRecruiterFields(Long id, RecruiterDto recruiterDto) {
        Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(id);
        if (optionalRecruiter.isPresent()) {
            Recruiter recruiter = optionalRecruiter.get();
            recruiter.setCompanyEmail(recruiterDto.getCompanyEmail());
            recruiter.setCompanyName(recruiterDto.getCompanyName());
            recruiter.setJobTitle(recruiterDto.getJobTitle());
            recruiter.setCompanyWebsite(recruiterDto.getCompanyWebsite());
            recruiter.setCompanyLocation(recruiterDto.getCompanyLocation());
            recruiter.setIndustry(recruiterDto.getIndustry());

        } else {
            throw new RuntimeException("Recruiter not found");
        }
    }
    public void saveProfilePicture(MultipartFile image, HttpServletRequest request) throws Exception {

        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);

        String contentType = image.getContentType();
        if (!isAllowedProfilePictureFormat(contentType)) {
            throw new RuntimeException("Invalid file format. Only PNG, JPG, and JPEG files are allowed.");
        }

        // Validate file size
        if (image.getSize() > MAX_PROFILE_PIC_SIZE) {
            throw new RuntimeException("File size exceeds the maximum limit of 2 MB.");
        }
        if(user.get().getProfileImageKey()!=null) {
            amazonS3.deleteObject(bucketName, getKeyFromUrl(user.get().getProfileImageKey().toString()));

        }
        String fileName = "profilepic"+user.get().getId() + "/" + image.getOriginalFilename();

        // Upload file to S3
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType()); // Use the MIME type from the uploaded file
        metadata.setContentLength(image.getSize());


        amazonS3.putObject(bucketName, fileName, image.getInputStream(), metadata);

        user.get().setProfileImageKey(new URL("https://anmol-workify-private.s3.ap-south-1.amazonaws.com/"+fileName.replace(" ", "+")));
        userRepository.save(user.get());
        if(recruiterRepository.existsByUser(user)){
            var recruiter = recruiterRepository.findByUser(user.get());
            recruiter.get().setProfileImage(user.get().getProfileImageKey());
            recruiterRepository.save(recruiter.get());
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
        recruiter.setProfileImage(user.get().getProfileImageKey());
        recruiterRepository.save(recruiter);
        return recruiter;
    }
    public static String getKeyFromUrl(String s3Url) throws Exception {
        // Parse the URL
        URL url = new URL(s3Url);

        // Extract the path from the URL
        String path = url.getPath();

        // Remove the leading '/' from the path to get the key
        return path.startsWith("/") ? path.substring(1) : path;
    }
    private boolean isAllowedProfilePictureFormat(String contentType) {
        return contentType != null && (
                contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg")
        );

    }

    public GetResponseRecruiter getCurrentRecruiter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (User) authentication.getPrincipal();
        Recruiter recruiter=recruiterRepository.findByUser(currentUser).get();
        GetResponseRecruiter getResponseRecruiter = new GetResponseRecruiter();
        getResponseRecruiter.setCompanyEmail(recruiter.getCompanyEmail());
        getResponseRecruiter.setCompanyName(recruiter.getCompanyName());
        getResponseRecruiter.setCompanyLocation(recruiter.getCompanyLocation());
        getResponseRecruiter.setCompanyWebsite(recruiter.getCompanyWebsite());
        getResponseRecruiter.setIndustry(recruiter.getIndustry());
        getResponseRecruiter.setJobTitle(recruiter.getJobTitle());
        getResponseRecruiter.setFirstName(recruiter.getUser().getFirstName());
        getResponseRecruiter.setLastName(recruiter.getUser().getLastName());
        getResponseRecruiter.setEmail(recruiter.getUser().getEmail());
        getResponseRecruiter.setPhone(recruiter.getUser().getMobile());
        getResponseRecruiter.setProfileImage(recruiter.getProfileImage());
        return getResponseRecruiter;
    }
}
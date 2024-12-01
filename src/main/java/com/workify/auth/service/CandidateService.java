package com.workify.auth.service;



import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.workify.auth.models.*;
import com.workify.auth.models.dto.CandidateDTO;
import com.workify.auth.models.dto.GetResponse;
import com.workify.auth.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CandidateService {
    private static final long MAX_PROFILE_PIC_SIZE = 2 * 1024 * 1024; // 2 MB
    private static final long MAX_CERTIFICATE_SIZE = 5 * 1024 * 1024; // 5 MB

    private final AmazonS3 amazonS3;
    @Value("${aws_bucket_name}")
    private String bucketName;

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private EducationRepository educationRepository;
    @Autowired
    private ExperienceRepository experienceRepository;
    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    public CandidateService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public  void deleteCertificateById(Long certificateId) throws Exception {

        if (!certificateRepository.existsById(certificateId)) {
            throw new RuntimeException("Certificate with ID " + certificateId + " does not exist");
        }
        amazonS3.deleteObject(bucketName, getKeyFromUrl(certificateRepository.findById(certificateId).get().getFileKey().toString()));
        certificateRepository.deleteById(certificateId);
    }

    public List<GetResponse> getAllCandidates() {
        List<Candidate> candidates = candidateRepository.findAll();

        // Map each Candidate to GetResponse
        return candidates.stream().map(candidate -> {
            GetResponse getResponse = new GetResponse();
            getResponse.setFirstName(candidate.getUser().getFirstName());
            getResponse.setLastName(candidate.getUser().getLastName());
            getResponse.setDOB(candidate.getDOB());
            getResponse.setEmail(candidate.getUser().getEmail());
            getResponse.setPhone(candidate.getUser().getMobile());
            getResponse.setEducation(candidate.getEducation());
            getResponse.setExperience(candidate.getExperiences());
            getResponse.setSkill(candidate.getSkills());
            getResponse.setCertificate(candidate.getCertificates());
            getResponse.setResumeKey(candidate.getResumeKey());
            getResponse.setProfileImageKey(candidate.getProfileImageKey());
            return getResponse;
        }).collect(Collectors.toList());
    }


public Map<String, Long> getStatistics(HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");
    final String username;
    String token = authHeader.replace("Bearer ", "");
    username=Jwtservice.extractusername(token);

    Optional<User> user= userRepository.findByUsername(username);
    Map<String, Long> statistics = new HashMap<>();
    long numberOfCandidates = candidateRepository.count();
    long numberOfRecruiters = recruiterRepository.count();
    long numberOfJobsPostedByRecruiters = jobRepository.count();
    //long numberOfJobsAppliedByCurrentCandidate = jobApplicationRepository.countByCandidate(candidateRepository.findByUser(user));

    statistics.put("numberOfCandidates", numberOfCandidates);
    statistics.put("numberOfRecruiters", numberOfRecruiters);
    statistics.put("total jobs", numberOfJobsPostedByRecruiters);
   // statistics.put("numberOfJobsAppliedByCurrentCandidate", numberOfJobsAppliedByCurrentCandidate);

    return statistics;
}
    public GetResponse getCandidateById(Long id) {
        var candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id " + id));


        GetResponse getResponse = new GetResponse();
        getResponse.setFirstName(candidate.getUser().getFirstName());
        getResponse.setLastName(candidate.getUser().getLastName());
        getResponse.setDOB(candidate.getDOB());
        getResponse.setEmail(candidate.getUser().getEmail());
        getResponse.setPhone(candidate.getUser().getMobile());
        getResponse.setEducation(candidate.getEducation());
        getResponse.setExperience(candidate.getExperiences());
        getResponse.setSkill(candidate.getSkills());
        getResponse.setCertificate(candidate.getCertificates());
        getResponse.setResumeKey(candidate.getResumeKey());
        getResponse.setProfileImageKey(candidate.getProfileImageKey());

        return getResponse;
    }



    public Candidate updateCandidate(CandidateDTO candidateDTO, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String token = authHeader.replace("Bearer ", "");
        final String username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);

        // Update fields only if they are provided
        if (candidateDTO.getSkill() != null) {
            candidate.setSkills(candidateDTO.getSkill());
        }

        if (candidateDTO.getDOB() != null) {
            candidate.setDOB(candidateDTO.getDOB());
        }

        // Update Education
        if (candidateDTO.getEducation() != null) {
            candidate.getEducation().clear();
            for (Education education : candidateDTO.getEducation()) {
                education.setCandidate(candidate);
                candidate.getEducation().add(education);
            }
        }

        // Update Experience
        if (candidateDTO.getExperience() != null) {
            candidate.getExperiences().clear();
            for (Experience experience : candidateDTO.getExperience()) {
                experience.setCandidate(candidate);
                candidate.getExperiences().add(experience);
            }
        }

        return candidateRepository.save(candidate);
    }

//    public List<Candidate> filterCandidates(String skill, Integer experiences) {
//        if (skill != null && experiences != null) {
//            return candidateRepository.findBySkillsContainingAndExperiencesGreaterThanEqual(skill, experiences);
//        } else if (experiences != null) {
//            return candidateRepository.findByExperiencesGreaterThanEqual( experiences);
//        } else if (skill != null) {
//            return candidateRepository.findBySkillsContaining(skill);
//        } else {
//            return candidateRepository.findAll();
//        }
//    }
//

@Transactional
    public void deleteCandidate(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        user.get().setRole(Role.USER);
        var candidate = candidateRepository.findByUser(user);
        certificateRepository.deleteAllByCandidate(candidate);
        experienceRepository.deleteAllByCandidate(candidate);
        candidateRepository.delete(candidate);
        userRepository.save(user.get());

    }

    public Candidate saveCandidateWithEducation(CandidateDTO candidateDTO, HttpServletRequest request) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        if(user.get().getRole()==Role.RECRUITER) {
            throw new RuntimeException("Recruiters cannot create candidate profiles");
        }
        if(candidateRepository.existsByUser(user)) {
            throw new RuntimeException("Candidate with Email " + username + " already exists");
        }
        return convertDTOToCandidate(candidateDTO, user);

    }

    private Candidate convertDTOToCandidate(CandidateDTO candidateDTO, Optional<User> user) throws IOException {
        Candidate candidate = new Candidate();
        candidate.setSkills(candidateDTO.getSkill());
        candidate.setDOB(candidateDTO.getDOB());
        candidate.setUser(user.orElse(null));
        List<Education> educations = candidateDTO.getEducation();
        if (educations != null) {
            for (Education education : educations) {
                education.setCandidate(candidate);
            }
            candidate.setEducation(educations);
        }

        List<Experience> experiences = candidateDTO.getExperience();
        if (experiences != null) {
            for (Experience experience: experiences) {
                experience.setCandidate(candidate);
            }
            candidate.setExperiences(experiences);
        }
        user.get().setRole(Role.CANDIDATE);
        userRepository.save(user.get());
        candidateRepository.save(candidate);
        return candidate;
    }

    public void saveCertificate(String certificateName, MultipartFile certificateFile, HttpServletRequest request) throws IOException {


        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);

        if (!Objects.equals(certificateFile.getContentType(), "application/pdf")) {
            throw new RuntimeException("Only PDF files are supported");
        }


        if (certificateFile.getSize() > MAX_CERTIFICATE_SIZE) {
            throw new RuntimeException("File size exceeds the maximum limit of 5 MB.");
        }
        if(certificateRepository.existsByCandidateAndCertificateName(candidateRepository.findByUser(user), certificateName)) {
            throw new RuntimeException("Certificate already exists");
        }



        String fileName = "certificates"+user.get().getId() + "/" + certificateFile.getOriginalFilename();


        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(certificateFile.getContentType()); // Use the MIME type from the uploaded file
        metadata.setContentLength(certificateFile.getSize());


        amazonS3.putObject(bucketName, fileName, certificateFile.getInputStream(), metadata);

        Certificate certificate = Certificate.builder()
                .certificateName(certificateName)
                .candidate(candidateRepository.findByUser(user))
                .fileKey(new URL("https://anmol-workify-private.s3.ap-south-1.amazonaws.com/"
                        + fileName.replace(" ", "+")))
                .build();
           certificateRepository.save(certificate);
    }

    public void saveResume(MultipartFile resume, HttpServletRequest request) throws Exception {

        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        if (!resume.getContentType().equals("application/pdf")) {
           throw new RuntimeException("Only PDF files are supported");
        }

        // Validate file size
        if (resume.getSize() > MAX_CERTIFICATE_SIZE) {
            throw new RuntimeException("File size exceeds the maximum limit of 5 MB.");
        }
        if(candidate.getResumeKey()!=null) {
            amazonS3.deleteObject(bucketName,getKeyFromUrl(candidate.getResumeKey().toString()));

        }
        String fileName = "resume"+user.get().getId() + "/" + resume.getOriginalFilename();

        // Upload file to S3
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(resume.getContentType()); // Use the MIME type from the uploaded file
        metadata.setContentLength(resume.getSize());


        amazonS3.putObject(bucketName, fileName, resume.getInputStream(), metadata);

        candidate.setResumeKey(new URL("https://anmol-workify-private.s3.ap-south-1.amazonaws.com/"+fileName.replace(" ", "+")));
        candidateRepository.save(candidate);

    }
@Transactional
    public void deleteCertificate(String certificateName, HttpServletRequest request) throws Exception {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        if (!certificateRepository.existsByCertificateName(certificateName)) {
            throw new RuntimeException("Certificate with name " + certificateName + " does not exist");
        }
        Certificate certi=certificateRepository.findByCertificateNameAndCandidate(certificateName, candidate);
        amazonS3.deleteObject(bucketName, getKeyFromUrl(certi.getFileKey().toString()));
        certificateRepository.deleteByCandidateAndCertificateName(candidate, certificateName);
    }
@Transactional
    public void deleteResume(HttpServletRequest request) throws Exception {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        amazonS3.deleteObject(bucketName, getKeyFromUrl(candidate.getResumeKey().toString()));
        candidate.setResumeKey(null);
        candidateRepository.save(candidate);
    }

    public void saveProfilePicture(MultipartFile image, HttpServletRequest request) throws Exception {

        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        String contentType = image.getContentType();
        if (!isAllowedProfilePictureFormat(contentType)) {
            throw new RuntimeException("Invalid file format. Only PNG, JPG, and JPEG files are allowed.");
        }

        // Validate file size
        if (image.getSize() > MAX_PROFILE_PIC_SIZE) {
            throw new RuntimeException("File size exceeds the maximum limit of 2 MB.");
        }
        if(candidate.getProfileImageKey()!=null) {
            amazonS3.deleteObject(bucketName, getKeyFromUrl(candidate.getProfileImageKey().toString()));

        }
        String fileName = "profilepic"+user.get().getId() + "/" + image.getOriginalFilename();

        // Upload file to S3
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType()); // Use the MIME type from the uploaded file
        metadata.setContentLength(image.getSize());


        amazonS3.putObject(bucketName, fileName, image.getInputStream(), metadata);

        candidate.setProfileImageKey(new URL("https://anmol-workify-private.s3.ap-south-1.amazonaws.com/"+fileName.replace(" ", "+")));
        candidateRepository.save(candidate);

    }
    @Transactional
    public void deleteAllCertificatesByCandidate(HttpServletRequest reques) {
        final String authHeader = reques.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix("certificates"+user.get().getId() + "/"); // Add "/" to match folder hierarchy

        ListObjectsV2Result result;

        do {
            result = amazonS3.listObjectsV2(request);

            // Loop through and delete each object
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                amazonS3.deleteObject(bucketName, objectSummary.getKey());
            }

            // If there are more objects, continue the process
            request.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        certificateRepository.deleteAllByCandidate(candidate);
    }

    private boolean isAllowedProfilePictureFormat(String contentType) {
        return contentType != null && (
                contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg")
        );

    }
    public static String getKeyFromUrl(String s3Url) throws Exception {
        // Parse the URL
        URL url = new URL(s3Url);

        // Extract the path from the URL
        String path = url.getPath();

        // Remove the leading '/' from the path to get the key
        return path.startsWith("/") ? path.substring(1) : path;
    }

    public GetResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var currentUser = (User) authentication.getPrincipal();
        Candidate candidate=candidateRepository.findByUser(Optional.ofNullable(currentUser));
        GetResponse getResponse = new GetResponse();
        getResponse.setFirstName(candidate.getUser().getFirstName());
        getResponse.setLastName(candidate.getUser().getLastName());
        getResponse.setDOB(candidate.getDOB());
        getResponse.setEmail(candidate.getUser().getEmail());
        getResponse.setPhone(candidate.getUser().getMobile());
        getResponse.setEducation(candidate.getEducation());
        getResponse.setExperience(candidate.getExperiences());
        getResponse.setSkill(candidate.getSkills());
        getResponse.setCertificate(candidate.getCertificates());
        getResponse.setResumeKey(candidate.getResumeKey());
        getResponse.setProfileImageKey(candidate.getProfileImageKey());

        return getResponse;
    }

    public void savePortfolio(MultipartFile portfolio, HttpServletRequest request) throws Exception {

        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        if (!portfolio.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are supported");
        }

        // Validate file size
        if (portfolio.getSize() > MAX_CERTIFICATE_SIZE) {
            throw new RuntimeException("File size exceeds the maximum limit of 5 MB.");
        }
        if(candidate.getPortfolioKey()!=null) {
            amazonS3.deleteObject(bucketName,getKeyFromUrl(candidate.getPortfolioKey().toString()));
        }
        String fileName = "portfolio"+user.get().getId() + "/" + portfolio.getOriginalFilename();

        // Upload file to S3
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(portfolio.getContentType()); // Use the MIME type from the uploaded file
        metadata.setContentLength(portfolio.getSize());


        amazonS3.putObject(bucketName, fileName, portfolio.getInputStream(), metadata);

        candidate.setResumeKey(new URL("https://anmol-workify-private.s3.ap-south-1.amazonaws.com/"+fileName.replace(" ", "+")));
        candidateRepository.save(candidate);

    }
}




package com.workify.auth.service;



import com.workify.auth.models.*;
import com.workify.auth.models.dto.CandidateDTO;
import com.workify.auth.models.dto.GetResponse;
import com.workify.auth.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CandidateService {

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

    public  void deleteCertificateById(Long certificateId) {
        if (!certificateRepository.existsById(certificateId)) {
            throw new RuntimeException("Certificate with ID " + certificateId + " does not exist");
        }
        certificateRepository.deleteById(certificateId);
    }

    public List<GetResponse> getAllCandidates() {
        List<Candidate> candidates = candidateRepository.findAll();

        // Map each Candidate to GetResponse
        return candidates.stream().map(candidate -> {
            GetResponse getResponse = new GetResponse();
            getResponse.setFirstName(candidate.getUser().getFirstName());
            getResponse.setLastName(candidate.getUser().getLastName());
            getResponse.setEmail(candidate.getUser().getEmail());
            getResponse.setPhone(candidate.getUser().getMobile());
            getResponse.setEducation(candidate.getEducation());
            getResponse.setExperience(candidate.getExperiences());
            getResponse.setSkill(candidate.getSkills());
            getResponse.setCertificate(candidate.getCertificates());
            getResponse.setResume(candidate.getResume());
            getResponse.setProfileImage(candidate.getProfileImage());
            return getResponse;
        }).collect(Collectors.toList());
    }



    public GetResponse getCandidateById(Long id) {
        var candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id " + id));


        GetResponse getResponse = new GetResponse();
        getResponse.setFirstName(candidate.getUser().getFirstName());
        getResponse.setLastName(candidate.getUser().getLastName());
        getResponse.setEmail(candidate.getUser().getEmail());
        getResponse.setPhone(candidate.getUser().getMobile());
        getResponse.setEducation(candidate.getEducation());
        getResponse.setExperience(candidate.getExperiences());
        getResponse.setSkill(candidate.getSkills());
        getResponse.setCertificate(candidate.getCertificates());
        getResponse.setResume(candidate.getResume());
        getResponse.setProfileImage(candidate.getProfileImage());

        return getResponse;
    }



    public Candidate updateCandidate(CandidateDTO candidateDTO, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String token = authHeader.replace("Bearer ", "");
        final String username = Jwtservice.extractusername(token);

        Optional<User> user = userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);

        candidate.setSkills(candidateDTO.getSkill());

        // Update Education
        List<Education> newEducations = candidateDTO.getEducations();
        candidate.getEducation().clear();  // Clear existing list instead of deleting
        if (newEducations != null) {
            for (Education education : newEducations) {
                education.setCandidate(candidate);  // Set the candidate for each education
                candidate.getEducation().add(education);  // Add to the existing list
            }
        }

        // Update Experience
        List<Experience> newExperiences = candidateDTO.getExperiences();
        candidate.getExperiences().clear();  // Clear existing list instead of deleting
        if (newExperiences != null) {
            for (Experience experience : newExperiences) {
                experience.setCandidate(candidate);  // Set the candidate for each experience
                candidate.getExperiences().add(experience);  // Add to the existing list
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


    public void deleteCandidate(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        candidateRepository.delete(candidate);
    }

    public Candidate saveCandidateWithEducation(CandidateDTO candidateDTO, HttpServletRequest request) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        return convertDTOToCandidate(candidateDTO, user);

    }

    private Candidate convertDTOToCandidate(CandidateDTO candidateDTO, Optional<User> user) throws IOException {
        Candidate candidate = new Candidate();
        candidate.setSkills(candidateDTO.getSkill());
        candidate.setUser(user.orElse(null));
        List<Education> educations = candidateDTO.getEducations();
        if (educations != null) {
            for (Education education : educations) {
                education.setCandidate(candidate);
            }
            candidate.setEducation(educations);
        }

        List<Experience> experiences = candidateDTO.getExperiences();
        if (experiences != null) {
            for (Experience experience: experiences) {
                experience.setCandidate(candidate);
            }
            candidate.setExperiences(experiences);
        }
        candidateRepository.save(candidate);
        return candidate;
    }

    public void saveCertificate(String certificateName, MultipartFile certificateFile, HttpServletRequest request) throws IOException {

        byte[] certificateData = certificateFile.getBytes();
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        if(certificateRepository.existsByCandidateAndCertificateName(candidateRepository.findByUser(user), certificateName)) {
            throw new RuntimeException("Certificate already exists");
        }


           Certificate certificate = Certificate.builder()
                   .certificateName(certificateName)
                   .certificateData(certificateData)
                   .candidate(candidateRepository.findByUser(user))
                   .build();
           certificateRepository.save(certificate);
    }

    public void saveResume(MultipartFile resume, HttpServletRequest request) throws IOException {
        byte[] resumeData = resume.getBytes();
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        candidate.setResume(resumeData);
        candidateRepository.save(candidate);

    }

    public void deleteCertificate(String certificateName, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        if (!certificateRepository.existsByCertificateName(certificateName)) {
            throw new RuntimeException("Certificate with name " + certificateName + " does not exist");
        }

        certificateRepository.deleteByCandidateAndCertificateName(candidate, certificateName);
    }

    public void deleteResume(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        candidate.setResume(null);
        candidateRepository.save(candidate);
    }

    public void saveProfilePicture(MultipartFile image, HttpServletRequest request) throws IOException {
        byte[] imageData = image.getBytes();
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        candidate.setProfileImage(imageData);
        candidateRepository.save(candidate);

    }
    @Transactional
    public void deleteAllCertificatesByCandidate(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        certificateRepository.deleteAllByCandidate(candidate);
    }
}


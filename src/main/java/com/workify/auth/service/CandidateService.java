package com.workify.auth.service;



import com.workify.auth.models.*;
import com.workify.auth.models.dto.CandidateDTO;
import com.workify.auth.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }


    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id " + id));
    }


    public Candidate updateCandidate( CandidateDTO candidateDTO, HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        String token = authHeader.replace("Bearer ", "");
        username=Jwtservice.extractusername(token);

        Optional<User> user= userRepository.findByUsername(username);
        var candidate = candidateRepository.findByUser(user);
        candidate.setSkills(candidateDTO.getSkill());
        educationRepository.deleteByCandidate( candidate );
        List<Education> educations = candidateDTO.getEducations();
        if (educations != null) {
            for (Education education : educations) {
                education.setCandidate(candidate);
            }
            candidate.setEducation(educations);
        }
        experienceRepository.deleteByCandidate( candidate );
        List<Experience> experiences = candidateDTO.getExperiences();
        if (experiences != null) {
            for (Experience experience : experiences) {
                experience.setCandidate(candidate);
            }
            candidate.setExperiences(experiences);
        }
        return candidateRepository.save(candidate);
    }



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
}


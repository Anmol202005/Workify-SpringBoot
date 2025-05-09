package com.workify.auth.repository;

import com.workify.auth.models.ApplicationStatus;
import com.workify.auth.models.Candidate;
import com.workify.auth.models.Job;
import com.workify.auth.models.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByApplicantId(Long applicantId);
    List<JobApplication> findByJobId(Long jobId);
    Boolean existsByJobAndApplicant(Job job, Candidate candidate);

    void deleteByJobId(Long jobId);
    long countByStatus(ApplicationStatus status);

    void deleteByApplicant(Candidate candidate);
    //long countByCandidate(Candidate candidate);
}

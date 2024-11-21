package com.workify.auth.repository;

import com.workify.auth.models.Candidate;
import com.workify.auth.models.Job;
import com.workify.auth.models.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByApplicantId(Long candidateId);
    List<JobApplication> findByJobId(Long jobId);
    Boolean existsByJobAndApplicant(Job job, Candidate candidate);
}

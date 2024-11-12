package com.workify.auth.repository;

import com.workify.auth.models.Recruiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Integer> {
    Page<Recruiter> findByCompanyNameContainingOrJobTitleContainingOrIndustryContaining(String companyName, String jobTitle, String industry, Pageable pageable);
    Page<Recruiter> findByCompanyNameContaining(String companyName, Pageable pageable);
    Page<Recruiter> findByJobTitleContaining(String jobTitle, Pageable pageable);
    Page<Recruiter> findByIndustryContaining(String industry, Pageable pageable);
    Optional<Recruiter> findByUserId(Integer userId);
}
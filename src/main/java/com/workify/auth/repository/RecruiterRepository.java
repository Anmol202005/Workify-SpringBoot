package com.workify.auth.repository;

import com.workify.auth.models.Recruiter;
import com.workify.auth.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
    List<Recruiter> findByCompanyNameContainingOrJobTitleContainingOrIndustryContaining(String companyName, String jobTitle, String industry);
    List<Recruiter> findByCompanyNameContaining(String companyName);
    List<Recruiter> findByJobTitleContaining(String jobTitle);
    List<Recruiter> findByIndustryContaining(String industry);
    Optional<Recruiter> findByUserId(Long userId);
    Optional<Recruiter> findByUser(User user);

    boolean existsByUser(Optional<User> user);
}
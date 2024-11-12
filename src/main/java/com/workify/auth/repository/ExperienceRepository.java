package com.workify.auth.repository;
import com.workify.auth.models.Candidate;
import com.workify.auth.models.Certificate;
import com.workify.auth.models.Education;

import com.workify.auth.models.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
public interface ExperienceRepository extends JpaRepository<Experience,Long > {
    @Modifying
    @Transactional
    @Query("DELETE FROM Experience c WHERE c.candidate = :candidate")
    void deleteByCandidate(Candidate candidate);
}

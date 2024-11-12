package com.workify.auth.repository;

import com.workify.auth.models.Candidate;
import com.workify.auth.models.Certificate;
import com.workify.auth.models.Education;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
public interface EducationRepository extends JpaRepository<Education,Long > {
    @Modifying
    @Transactional
    @Query("DELETE FROM Education c WHERE c.candidate = :candidate")
    void deleteByCandidate(Candidate candidate);
}
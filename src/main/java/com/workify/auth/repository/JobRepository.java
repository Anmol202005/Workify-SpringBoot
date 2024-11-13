package com.workify.auth.repository;

import com.workify.auth.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    List<Job> findByTitleContaining(String title);
    List<Job> findByLocationContaining(String location);
    List<Job> findByIndustryContaining(String industry);
}
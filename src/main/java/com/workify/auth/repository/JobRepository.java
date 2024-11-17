package com.workify.auth.repository;

import com.workify.auth.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByTitleContaining(String title);
    List<Job> findByLocationContaining(String location);
    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN j.requiredSkills skill WHERE " +
            "LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(j.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(skill) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Job> searchJobs(String keyword);
    List<Job> findByPostedById(Long recruiterId);
    List<Job> findByMinSalaryGreaterThanEqualAndMaxSalaryLessThanEqual(Integer minSalary, Integer maxSalary);
    List<Job> findByTitleContainingAndLocationContaining(String title, String location);
    List<Job> findByTitleContainingAndLocationContainingAndMinSalaryGreaterThanEqualAndMaxSalaryLessThanEqual(String title, String location, Integer minSalary, Integer maxSalary);

}

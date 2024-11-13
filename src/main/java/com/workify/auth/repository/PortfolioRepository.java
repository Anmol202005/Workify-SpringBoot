package com.workify.auth.repository;

import com.workify.auth.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Portfolio findByUserId(Long userId);
}
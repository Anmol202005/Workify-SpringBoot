package com.workify.auth.repository;

import com.workify.auth.models.Candidate;
import com.workify.auth.models.Certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
public interface CertificateRepository extends JpaRepository<Certificate,Long > {

    Boolean existsByCandidateAndCertificateName(Candidate candidate, String certificateName);
    @Modifying
    @Transactional
    @Query("DELETE FROM Certificate e WHERE e.candidate = :candidate AND e.certificateName = :certificateName")
    void deleteByCandidateAndCertificateName(Candidate candidate, String certificateName);
    Boolean existsByCertificateName(String name);
    void deleteAllByCandidate(Candidate candidate);


}

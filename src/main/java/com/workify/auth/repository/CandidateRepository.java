package com.workify.auth.repository;
import com.workify.auth.models.Candidate;
import com.workify.auth.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CandidateRepository extends JpaRepository<Candidate,Long >  {
    Candidate findByUser(Optional<User> user);
    @Query("SELECT DISTINCT c FROM Candidate c JOIN c.skills skill " +
            "WHERE LOWER(skill) IN :keywords")
    List<Candidate> findCandidatesBySkills(@Param("keywords") List<String> keywords);
    Boolean existsByUser(Optional<User> user);
    long count();
}

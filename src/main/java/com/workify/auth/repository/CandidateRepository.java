package com.workify.auth.repository;
import com.workify.auth.models.Candidate;
import com.workify.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CandidateRepository extends JpaRepository<Candidate,Long >  {
    Candidate findByUser(Optional<User> user);
}

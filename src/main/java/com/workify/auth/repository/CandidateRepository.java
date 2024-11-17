package com.workify.auth.repository;
import com.workify.auth.models.Candidate;
import com.workify.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CandidateRepository extends JpaRepository<Candidate,Long >  {
    Candidate findByUser(Optional<User> user);
    List<Candidate> findBySkillsContaining(String skill);
    List<Candidate> findByLocationContaining(String location);
    List<Candidate> findByExperienceGreaterThanEqual(Integer experience);
    List<Candidate> findBySkillsContainingAndLocationContaining(String skill, String location);
    List<Candidate> findBySkillsContainingAndExperienceGreaterThanEqual(String skill, Integer experience);
    List<Candidate> findByLocationContainingAndExperienceGreaterThanEqual(String location, Integer experience);
    List<Candidate> findBySkillsContainingAndLocationContainingAndExperienceGreaterThanEqual(String skill, String location, Integer experience);

}

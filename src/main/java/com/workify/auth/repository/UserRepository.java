package com.workify.auth.repository;

import com.workify.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long > {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
    Optional<User> findByUsernameAndVerified(String username, boolean verified);
    Optional<User> findByEmailAndVerified(String email, boolean verified);
    Optional<User> findByMobileAndVerified(String mobile, boolean verified);
   // List<User> findByUniqueUser(String username);
    boolean existsByMobile(String mobile);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameAndVerified(String username, boolean verified);

    Optional<User> findByUser(User username);
}

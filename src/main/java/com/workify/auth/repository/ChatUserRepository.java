package com.workify.auth.repository;

import com.workify.auth.models.ChatUser;
import com.workify.auth.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatUserRepository extends JpaRepository<ChatUser, String> {
    List<ChatUser> findByStatus(Status status);
}

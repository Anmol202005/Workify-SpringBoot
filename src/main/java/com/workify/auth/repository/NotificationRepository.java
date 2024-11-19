package com.workify.auth.repository;

import com.workify.auth.models.Job;
import com.workify.auth.models.Notification;
import com.workify.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);

}
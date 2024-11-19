package com.workify.auth.service;

import com.workify.auth.models.Notification;
import com.workify.auth.models.User;
import com.workify.auth.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public  List<Notification>  getNotification() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return notificationRepository.findByUser(currentUser);
    }
}

package com.workify.auth.service;

import com.workify.auth.models.Status;
import com.workify.auth.models.User;
import com.workify.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WebSocketService {
    private final UserRepository userRepository;
    public void saveuser(User user){

        user.setStatus(Status.ONLINE);
         userRepository.save(user);
    }
    public void disconnect(User user){

        user.setStatus(Status.OFFLINE);
        userRepository.save(user);
    }
    public List<User> findConnectedUser(){
        return userRepository.findByStatus(Status.ONLINE);
    }
}

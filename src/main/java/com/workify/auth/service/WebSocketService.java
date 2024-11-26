package com.workify.auth.service;

import com.workify.auth.models.ChatUser;
import com.workify.auth.models.Status;
import com.workify.auth.models.User;
import com.workify.auth.repository.ChatUserRepository;
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
    private final ChatUserRepository chatUserRepository;

    public void saveuser(ChatUser user){

        user.setStatus(Status.ONLINE);
         chatUserRepository.save(user);
    }
    public void disconnect(ChatUser user){

        user.setStatus(Status.OFFLINE);
        chatUserRepository.save(user);
    }
    public List<ChatUser> findConnectedUser(){
        return chatUserRepository.findByStatus(Status.ONLINE);
    }
}

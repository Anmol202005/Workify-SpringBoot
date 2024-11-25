package com.workify.auth.service;

import com.workify.auth.repository.community.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    CommunityRepository communityRepository;
    public Optional<String > getChatRoomId(
            String senderId,
            String receiverId,
            Boolean roomExist
    ){return null;}
}

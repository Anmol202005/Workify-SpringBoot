package com.workify.auth.service;

import com.workify.auth.models.community.ChatMessage;
import com.workify.auth.models.community.Community;
import com.workify.auth.repository.community.ChatMessageRepository;
import com.workify.auth.repository.community.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private CommunityRepository communityRepository;

    public ChatMessage sendMessage(Long communityId, ChatMessage message) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (!community.getMembers().contains(message.getSender())) {
            throw new IllegalArgumentException("User is not a member of this community.");
        }

        message.setCommunity(community);
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getChatHistory(Long communityId, String userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (!community.getMembers().contains(userId)) {
            throw new IllegalArgumentException("Access denied: User is not a member of this community.");
        }

        return chatMessageRepository.findByCommunityId(communityId);
    }
}

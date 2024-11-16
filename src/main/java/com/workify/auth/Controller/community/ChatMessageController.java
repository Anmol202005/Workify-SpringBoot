package com.workify.auth.Controller.community;

import com.workify.auth.models.community.ChatMessage;
import com.workify.auth.models.community.Community;
import com.workify.auth.repository.community.ChatMessageRepository;
import com.workify.auth.repository.community.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/communities//messages")
public class ChatMessageController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @MessageMapping("/sendMessage/{communityId}")
    @SendTo("/topic/messages/{communityId}")
    public ChatMessage sendMessage(@PathVariable Long communityId, ChatMessage message) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (!community.getMembers().contains(message.getSender())) {
            throw new IllegalArgumentException("User is not a member of this community.");
        }

        message.setCommunity(community);
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }


    @GetMapping("/{communityId}/messages")
    public ResponseEntity<?> getChatHistory(@PathVariable Long communityId, @RequestParam String userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (!community.getMembers().contains(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: User is not a member of this community.");
        }

        List<ChatMessage> messages = chatMessageRepository.findByCommunityId(communityId);
        return ResponseEntity.ok(messages);
    }



}


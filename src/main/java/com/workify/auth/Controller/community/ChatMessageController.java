package com.workify.auth.Controller.community;

import com.workify.auth.models.community.ChatMessage;
import com.workify.auth.models.community.Community;
import com.workify.auth.repository.community.ChatMessageRepository;
import com.workify.auth.repository.community.CommunityRepository;
import com.workify.auth.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/sendMessage/{communityId}")
    @SendTo("/topic/messages/{communityId}")
    public ChatMessage sendMessage(@PathVariable Long communityId,  ChatMessage message) {
        return chatMessageService.sendMessage(communityId, message);
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long communityId, @RequestParam String userId) {
        try {
            List<ChatMessage> messages = chatMessageService.getChatHistory(communityId, userId);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}
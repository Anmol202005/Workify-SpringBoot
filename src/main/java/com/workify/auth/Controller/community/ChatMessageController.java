package com.workify.auth.Controller.community;

import com.workify.auth.config.WebSocketConfig;
import com.workify.auth.models.ChatUser;
import com.workify.auth.models.User;
import com.workify.auth.service.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
public class ChatMessageController {
    private final WebSocketService webSocketService;

    @MessageMapping("/user.addUser")
    @SendTo("/user/topic")
    public ChatUser addUser(@Payload ChatUser user) {
         webSocketService.saveuser(user);
         return user;
    }
    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/topic")
    public ChatUser disconnectUser(@Payload ChatUser user) {
        webSocketService.disconnect(user);
        return user;
    }
    @GetMapping("/active-user")
    public ResponseEntity<List<ChatUser>> getActiveUser() {
        return ResponseEntity.ok(webSocketService.findConnectedUser());
    }

}
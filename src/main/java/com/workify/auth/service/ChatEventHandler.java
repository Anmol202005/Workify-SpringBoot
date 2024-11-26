package com.workify.auth.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.workify.auth.models.ChatMessage;
import com.workify.auth.models.ChatNotification;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatEventHandler {

    private final SocketIOServer server;

    // To store connected clients
    private final ConcurrentHashMap<String, String> userSocketMap = new ConcurrentHashMap<>();

    public ChatEventHandler(SocketIOServer server) {
        this.server = server;
    }

    @OnConnect
    public void onConnect(com.corundumstudio.socketio.SocketIOClient client) {
        String userId = client.getHandshakeData().getSingleUrlParam("userId");
        userSocketMap.put(userId, client.getSessionId().toString());
        System.out.println("User connected: " + userId);
    }

    @OnDisconnect
    public void onDisconnect(com.corundumstudio.socketio.SocketIOClient client) {
        userSocketMap.values().remove(client.getSessionId().toString());
        System.out.println("User disconnected: " + client.getSessionId());
    }

    @OnEvent("message")
    public void onMessage(com.corundumstudio.socketio.SocketIOClient client, ChatNotification message) {
        String recipientSocketId = userSocketMap.get(message.getRecipientId());
        if (recipientSocketId != null) {
            server.getClient(UUID.fromString(recipientSocketId)).sendEvent("message", message);
        }
    }
}


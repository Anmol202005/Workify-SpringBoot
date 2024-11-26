package com.workify.auth.service;

import com.workify.auth.models.ChatMessage;
import com.workify.auth.models.ChatNotification;
import com.workify.auth.repository.community.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        var chatId=chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(),true).orElseThrow();
        chatMessage.setChatId(chatId);
        return chatMessageRepository.save(chatMessage);
    }
    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatId=chatRoomService.getChatRoomId(senderId, recipientId, true);
        return chatId.map(chatMessageRepository::findByChatId).orElse(new ArrayList<>());

    }

}
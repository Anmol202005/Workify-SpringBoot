package com.workify.auth.service;

import com.workify.auth.models.ChatRoom;
import com.workify.auth.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    public Optional<String > getChatRoomId(
            String senderId,
            String recipientId,
            Boolean roomNotExist
    ){return chatRoomRepository.findBySenderIdAndRecipientId(senderId,recipientId).map(ChatRoom::getChatId)
            .or(() -> {
                if(roomNotExist){
                    var chatId=createChatId(senderId,recipientId);
                    return Optional.of(chatId);
                }
                return Optional.empty();
            });}

    private String createChatId(String senderId, String recipientId) {
        var chatId=String.format("%s_%s", senderId, recipientId);
        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);
        return chatId;

    }
}

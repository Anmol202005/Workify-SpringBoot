package com.workify.auth.repository.community;

import com.workify.auth.models.ChatMessage;
import com.workify.auth.models.ChatNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {


    ArrayList<ChatMessage> findByChatId(String chatId);
}

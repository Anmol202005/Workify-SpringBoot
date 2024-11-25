package com.workify.auth.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ChatRoom {
    private String id;
    private String chatId;
    private String senderId;
    private String receiverId;

}

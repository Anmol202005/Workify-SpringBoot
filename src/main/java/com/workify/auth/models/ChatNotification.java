package com.workify.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification  {
    private String id;
    private String senderId;
    private String recipientId;
    private String content;

}

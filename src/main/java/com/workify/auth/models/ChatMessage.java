package com.workify.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Time;
import java.util.Date;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @ToString
    @Builder
    @Entity
    public  class ChatMessage {
        @Id
        private String id;
        private String chatId;
        private String senderId;
        private String recipientId;
        private String Content;
        private Date date;
        private Time time;
    }


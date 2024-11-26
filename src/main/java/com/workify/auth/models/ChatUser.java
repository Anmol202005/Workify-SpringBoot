package com.workify.auth.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ChatUser {
    @Id
    private String nickName;
    private String fullName;
    private Status status;
}


package com.workify.auth.models.dto;

import com.workify.auth.models.User;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String message;
    private User user;
}

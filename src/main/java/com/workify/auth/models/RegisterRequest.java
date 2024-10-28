package com.workify.auth.models;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Nonnull
    private String firstName;
    @Nonnull
    private String lastName;
    @Nonnull
    private String username;
    @Nonnull
    private String contact;
    @Nonnull
    private String password;
}

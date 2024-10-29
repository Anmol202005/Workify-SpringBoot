package com.workify.auth.models;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateForgotPasswordRequest {
    private String username;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}

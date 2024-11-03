package com.workify.auth.models;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateForgotPasswordRequest {
    private String contact;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}

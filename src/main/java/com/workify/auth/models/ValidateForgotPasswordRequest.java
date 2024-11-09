package com.workify.auth.models;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateForgotPasswordRequest {
    private String contact;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters, with one uppercase, one lowercase, one number, and one special character")
    private String newPassword;
    private String confirmPassword;
}

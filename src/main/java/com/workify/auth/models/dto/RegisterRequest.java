package com.workify.auth.models.dto;

import com.workify.auth.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull(message = "First Name can't be empty")
    @Size(min = 1,message = "first name can't be empty")
    private String firstName;
    private String lastName;
//    private String username;
    @Email(message = "invalid Email")
    private String email;
    @Pattern(regexp = "^\\+?\\d{10,15}$", message = "invalid Mobile")
    private String mobile;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters, with one uppercase, one lowercase, one number, and one special character")
    private String password;
    private Role role;
}

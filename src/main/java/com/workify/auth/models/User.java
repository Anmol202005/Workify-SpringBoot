package com.workify.auth.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder

@Entity
@Table(name = "app_user")
public class User implements UserDetails {
    @Getter
    @Id
    @GeneratedValue
    private Integer id;
    @NotNull(message = "First name cannot be null")
    @Size(max = 20, message = "First name should not be more than 20 characters")
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String mobile;
    private String password;
    @Enumerated(EnumType.STRING)
    @JsonBackReference
    private Role role;
    private String otp;
    private LocalDateTime otpGenerated;
    private Boolean changepassOTP = false;
    private Boolean verified;
    private LocalDateTime resendOtpTimer;
    private LocalDateTime registerRequestTimer;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

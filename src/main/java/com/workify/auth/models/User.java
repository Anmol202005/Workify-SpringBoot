package com.workify.auth.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.net.URL;
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
    private Long id;
    @NotNull(message = "First name cannot be null")
    @Size(max = 20, message = "First name should not be more than 20 characters")
    private String firstName;
    private String lastName;
    @JsonIgnore
    private String username;
    private String email;
    private String mobile;
    private Status status;
    private Boolean membership;
    @JsonIgnore
    private URL profileImageKey;
    @JsonIgnore
    private String password;
    @Enumerated(EnumType.STRING)

    private Role role;
    @JsonIgnore
    private String otp;
    @JsonIgnore
    private LocalDateTime otpGenerated;
    @JsonIgnore
    private Boolean changepassOTP = false;
    @JsonIgnore
    private Boolean verified;
    @JsonIgnore
    private LocalDateTime resendOtpTimer;
    @JsonIgnore
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

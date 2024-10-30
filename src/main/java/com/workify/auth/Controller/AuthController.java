package com.workify.auth.Controller;

import com.workify.auth.models.*;
import com.workify.auth.service.AuthService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.register(request));

    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return ResponseEntity.ok(service.authenticate(request));
    }
    @PostMapping("/validate")
    public ResponseEntity<AuthenticationResponse> validate(
            @RequestBody OtpValidate request
    ){
        return ResponseEntity.ok(service.validate(request));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.forgotPassword(request.getUsername()));
    }
    @PutMapping("/verify-otp")
    public ResponseEntity<String> verify(
            @RequestBody ValidateForgotPasswordRequest otp
    ){
        return ResponseEntity.ok(service.verifyForgotPassword(otp));
    }
}
package com.workify.auth.Controller;

import com.workify.auth.models.AuthenticationRequest;
import com.workify.auth.models.AuthenticationResponse;
import com.workify.auth.models.OtpValidate;
import com.workify.auth.models.RegisterRequest;
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
    @PutMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @RequestBody String username
    ) throws MessagingException {
        return ResponseEntity.ok(service.forgotPassword(username));
    }
}
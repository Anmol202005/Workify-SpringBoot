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
    public ResponseEntity<ResponseMessage> register(
            @RequestBody RegisterRequest request
    )  {
        return service.register(request);

    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ){
        return service.authenticate(request);
    }
    @PostMapping("/validate")
    public ResponseEntity<AuthenticationResponse> validate(
            @RequestBody OtpValidate request
    ){
        return service.validate(request);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseMessage> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) throws MessagingException {
        return service.forgotPassword(request.getContact());
    }
    @PutMapping("/verify-otp")
    public ResponseEntity<ResponseMessage> verify(
            @RequestBody ValidateForgotPasswordRequest otp
    ){
        return service.verifyForgotPassword(otp);
    }
}
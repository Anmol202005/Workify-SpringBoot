package com.workify.auth.Controller;

import com.workify.auth.models.User;
import com.workify.auth.models.dto.*;
import com.workify.auth.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthService service;
    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> register(
          @Valid @RequestBody RegisterRequest request
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
    @PutMapping("/change-password")
    public ResponseEntity<ResponseMessage> verify(
           @Valid @RequestBody ValidateForgotPasswordRequest otp
    ){
        return service.verifyForgotPassword(otp);
    }
    @PostMapping("/verify-otp-forgotpassword")
    public ResponseEntity<ResponseMessage> verifyOtpForgotPassword(
            @RequestBody OtpValidate request
    ){
        return service.verifyOtpForgotPassword(request);
    }

}
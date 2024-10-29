package com.workify.auth.service;

import com.workify.auth.models.*;
import com.workify.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Jwtservice jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    public String register(RegisterRequest request) throws MessagingException {

        if(repository.existsByUsername(request.getUsername())){
            return ("Username already exists");
        }

        if(repository.existsByEmail(request.getEmail())){
            return ("Email already exists");
        }
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CANDIDATE)
                .build();


        String otp= generateotp();
        user.setOtp(otp);
        repository.save(user);
        sendVerificationEmail(user.getEmail(),otp);

        return ("OTP sent to "+user.getEmail());

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));
        var user=repository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    private String generateotp(){
        Random random=new Random();
       int otpvalue= 100000+random.nextInt(900000);
       return String.valueOf(otpvalue);
    }
    private void sendVerificationEmail(String Email,String otp) throws MessagingException {
        String subject="Verification mail";
        String body="Your verification code is "+otp;
        emailService.sendEmail(Email,subject,body);
    }
    public AuthenticationResponse generateToken(User user) {
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse validate(OtpValidate request) {
        var user=repository.findByUsername(request.getUsername()).orElseThrow();
        if(user.getOtp().equals(request.getOtp())){
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }else {
            throw new RuntimeException("Invalid OTP provided.");
        }

    }

    public String forgotPassword(String username) throws MessagingException {
        var user = repository.findByUsername(username).orElseThrow();
        String otp= generateotp();
        user.setOtp(otp);
        repository.save(user);
        sendVerificationEmail(user.getEmail(),otp);
        return ("OTP sent to "+user.getEmail());
    }

    public String verifyForgotPassword(ValidateForgotPasswordRequest request) {
        var user=repository.findByUsername(request.getUsername()).orElseThrow();
        if(user.getOtp().equals(request.getOtp()) && request.getNewPassword().equals(request.getConfirmPassword()) ){
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            repository.save(user);
            return ("Password changed successfully");
        }
        else {
            throw new RuntimeException("Invalid OTP provided.");
        }
    }
}

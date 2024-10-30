package com.workify.auth.service;

import com.workify.auth.models.*;
import com.workify.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Jwtservice jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    public ResponseMessage register(RegisterRequest request)  {

        if(repository.existsByUsername(request.getUsername())){
            Optional<User> userOptional = repository.findByUsername(request.getUsername());

            if (userOptional.isPresent() && userOptional.get().getVerified()) {
                return ResponseMessage.builder()
                        .message("Username already exists")
                        .build();
            }

        }

        if(repository.existsByEmail(request.getEmail())){
            Optional<User> userOptional = repository.findByUsername(request.getUsername());

            if (userOptional.isPresent() && userOptional.get().getVerified()) {
                return ResponseMessage.builder()
                        .message("Email already exists")
                        .build();
            }
        }
        if (request.getUsername().length() >= 15 || request.getUsername().length() < 5) {
            return ResponseMessage.builder()
                    .message("Username must be less than 15 characters and greater than 5")
                    .build();
        }
        if (request.getUsername().contains(" ")) {
            return ResponseMessage.builder()
                    .message("Username must not contain spaces")
                    .build();
        }
        if(request.getFirstName().isEmpty()){
            return ResponseMessage.builder()
                    .message("First name is required")
                    .build();
        }
        if(request.getFirstName().length()>20){
            return ResponseMessage.builder()
                    .message("First name can not be longer than 20 characters")
                    .build();
        }
        if(request.getLastName().length()>20){
            return ResponseMessage.builder()
                    .message("Last name can not be longer than 20 characters")
                    .build();
        }

        if(request.getEmail().isEmpty()){
            return ResponseMessage.builder()
                    .message("Email is required")
                    .build();
        }

        String password = request.getPassword();
        if (password.length() < 8 || password.length()>20) {
            return ResponseMessage.builder()
                    .message("Password must greater than 8 characters and less than 20 characters")
                    .build();
        }
        if (!password.matches(".*[A-Z].*")) {
            return ResponseMessage.builder()
                    .message("Password must contain at least one uppercase letter")
                    .build();
        }
        if (!password.matches(".*[a-z].*")) {
            return ResponseMessage.builder()
                    .message("Password must contain at least one lowercase letter")
                    .build();
        }
        if (!password.matches(".*\\d.*")) {
            return ResponseMessage.builder()
                    .message("Password must contain at least one digit")
                    .build();
        }
        if (!password.matches(".*[!@#$%^&*()\\-_=+{};:,<.>].*")) {
            return ResponseMessage.builder()
                    .message("Password must contain at least one special character")
                    .build();
        }
        if (password.contains(" ")) {
            return ResponseMessage.builder()
                    .message("Password must not contain spaces")
                    .build();
        }
        Role role = request.getRole() != null ? request.getRole() : Role.CANDIDATE;

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(request.getUsername())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .verified(false)
                .build();


        String otp= generateotp();
        user.setOtp(otp);
        user.setOtpGenerated(LocalDateTime.now());
        repository.save(user);
        try {
            sendVerificationEmail(user.getEmail(), otp);
        } catch (MessagingException e) {
            return ResponseMessage.builder()
                    .message("Failed to send verification email.")
                    .build();
        }

        return ResponseMessage.builder()
                .message("OTP sent to "+user.getEmail())
                .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if (request.getUsername().length() >= 15 || request.getUsername().length() < 5) {
            return  AuthenticationResponse.builder()
                    .token(null)
                    .message("Username must be less than 15 characters and greater than 5")
                    .build();
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));
        var user=repository.findByUsername(request.getUsername()).orElseThrow();
        user.setVerified(true);
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Token Generated Successfully")
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
        if (request.getUsername().length() >= 15 || request.getUsername().length() < 5) {
            return  AuthenticationResponse.builder()
                    .token(null)
                    .message("Username must be less than 15 characters and greater than 5")
                    .build();
        }

        var user=repository.findByUsername(request.getUsername()).orElseThrow();
        long minuteElapsed = ChronoUnit.MINUTES.between(user.getOtpGenerated(), LocalDateTime.now());
        if(user.getOtp().equals(request.getOtp()) && minuteElapsed < 5){
            user.setVerified(true);
            repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }else {
            throw new RuntimeException("Invalid OTP provided.");
        }

    }

    public ResponseMessage forgotPassword(String username)  {
        if (username.length() >= 15 || username.length() < 5) {
            return ResponseMessage.builder()
                    .message("Username should be greater than 5 characters and less than 15")
                    .build();
        }
        var user = repository.findByUsername(username).orElseThrow();
        String otp= generateotp();
        user.setOtp(otp);
        repository.save(user);
        try {
            sendVerificationEmail(user.getEmail(), otp);
        } catch (MessagingException e) {
            return ResponseMessage.builder()
                    .message("Failed to send verification email.")
                    .build();
        }
//        return ("OTP sent to "+user.getEmail());
        return ResponseMessage.builder()
                .message("OTP sent to "+user.getEmail())
                .build();
    }

    public ResponseMessage verifyForgotPassword(ValidateForgotPasswordRequest request) {
        if (request.getUsername().length() >= 15 || request.getUsername().length() < 5) {
            return ResponseMessage.builder()
                    .message("Username must be less than 15 characters and greater than 5")
                    .build();
        }
        String password = request.getNewPassword();
        if (password.length() < 8 || password.length()>20) {
            return ResponseMessage.builder()
                    .message("Password must greater than 8 characters and less than 20 characters")
                    .build();
        }
        if (!password.matches(".*[A-Z].*")) {
            return ResponseMessage.builder()
                    .message("Password must contain at least one uppercase letter")
                    .build();
        }
        if (!password.matches(".*[a-z].*")) {
            return ResponseMessage.builder()
                    .message("Password must contain at least one lowercase letter")
                    .build();
        }
        if (!password.matches(".*\\d.*")) {
            return ResponseMessage.builder()
                    .message("Password must contain at least one digit")
                    .build();
        }
        if (!password.matches(".*[!@#$%^&*()\\-_=+{};:,<.>].*")) {
            return ResponseMessage.builder()
                    .message("Password must contain at least one special character")
                    .build();
        }
        if (password.contains(" ")) {
            return ResponseMessage.builder()
                    .message("Password must not contain spaces")
                    .build();
        }

        var user=repository.findByUsername(request.getUsername()).orElseThrow();
        if(user.getOtp().equals(request.getOtp()) && request.getNewPassword().equals(request.getConfirmPassword()) ){
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            repository.save(user);
            return ResponseMessage.builder()
                    .message("Password changed successfully")
                    .build();
        }
        else {
            if(!user.getOtp().equals(request.getOtp()))
            {return ResponseMessage.builder()
                        .message("OTP invalid")
                        .build();
                }
            else{
                return ResponseMessage.builder()
                        .message("Confirm Password not same as New password")
                        .build();
            }
        }
    }
}

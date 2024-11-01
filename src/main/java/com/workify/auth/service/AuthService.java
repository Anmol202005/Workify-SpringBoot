package com.workify.auth.service;

import com.workify.auth.models.*;
import com.workify.auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final TwilioService twilioService;
    public ResponseMessage register(RegisterRequest request)  {

        if(repository.existsByUsername(request.getUsername())){
            Optional<User> userOptional = repository.findByUsernameAndVerified(request.getUsername() , true);

            if (userOptional.isPresent() && userOptional.get().getVerified()) {
                return ResponseMessage.builder()
                        .message("Username already exists")
                        .build();
            }

        }

        if(repository.existsByEmail(request.getEmail()) && request.getEmail()!=null && !request.getEmail().isEmpty()){
            Optional<User> userOptional = repository.findByEmailAndVerified(request.getEmail() , true);

            if (userOptional.isPresent() && userOptional.get().getVerified()) {
                return ResponseMessage.builder()
                        .message("Email already exists")
                        .build();
            }
        }
        if(repository.existsByMobile(request.getMobile()) && request.getMobile()!=null && !request.getMobile().isEmpty()){
            Optional<User> userOptional = repository.findByMobileAndVerified(request.getMobile(),true);

            if (userOptional.isPresent() && userOptional.get().getVerified()) {
                return ResponseMessage.builder()
                        .message("Mobile already exists")
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

        if(request.getEmail()==null && request.getMobile().isEmpty()){
            return ResponseMessage.builder()
                    .message("Either Email or Mobile number is required")
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
        if(user.getEmail()!=null){
           return sendVerificationEmail(user.getEmail(), otp);
        }
        else{
           return twilioService.sendOtp(request.getMobile(), otp);

        }

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if (request.getUsername().length() >= 15 || request.getUsername().length() < 5) {
            return  AuthenticationResponse.builder()
                    .token(null)
                    .message("Username must be less than 15 characters and greater than 5")
                    .build();
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            return AuthenticationResponse.builder()
                    .message("Incorrect username or password")
                    .build();
        }

        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        user.setVerified(true);
        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Login successful")
                .build();

    }
    private String generateotp(){
        Random random=new Random();
       int otpvalue= 100000+random.nextInt(900000);
       return String.valueOf(otpvalue);
    }
    public ResponseMessage sendVerificationEmail(String email, String otp) {
        String subject = "Verification Mail";
        String body = "Your verification code is " + otp;

        return emailService.sendEmail(email, subject, body);
        // Return success response if email is sent successfully

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
                    .message("Username must be less than 15 characters and greater than 5")
                    .build();
        }
        if(repository.existsByUsername(request.getUsername())){
            Optional<User> userOptional = repository.findByUsername(request.getUsername());

            if (userOptional.isPresent() && userOptional.get().getVerified()) {
                return AuthenticationResponse.builder()
                        .message("Username already exists")
                        .build();
            }

        }

        var user=repository.findByUsername(request.getUsername()).orElseThrow();
        long minuteElapsed = ChronoUnit.MINUTES.between(user.getOtpGenerated(), LocalDateTime.now());
        if(user.getOtp().equals(request.getOtp()) && minuteElapsed < 5){
            user.setVerified(true);
            repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .message("Account has been registered successfully")
                    .token(jwtToken)
                    .build();
        }else {
            return AuthenticationResponse.builder()
                    .message("invalid OTP")
                    .build();
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
        user.setOtpGenerated(LocalDateTime.now());
        repository.save(user);
        if(user.getEmail()!=null){
            return sendVerificationEmail(user.getEmail(), otp);
        }
        else{
            return twilioService.sendOtp(user.getMobile(), otp);

        }
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
        long minuteElapsed = ChronoUnit.MINUTES.between(user.getOtpGenerated(), LocalDateTime.now());
        if(user.getOtp().equals(request.getOtp()) && request.getNewPassword().equals(request.getConfirmPassword()) && minuteElapsed < 5 ){
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

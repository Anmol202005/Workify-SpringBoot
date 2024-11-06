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
    public ResponseEntity register(RegisterRequest request)  {

//        if(repository.existsByUsername(request.getUsername())){
//            Optional<User> userOptional = repository.findByUsernameAndVerified(request.getUsername() , true);
//
//            if (userOptional.isPresent() && userOptional.get().getVerified()) {
//                return ResponseMessage.builder()
//                        .message("Username already exists")
//                        .build();
//            }
//
//        }



        if(repository.existsByEmail(request.getEmail()) && request.getEmail()!=null && !request.getEmail().isEmpty()){
            Optional<User> userOptional = repository.findByEmailAndVerified(request.getEmail() , true);

            if (userOptional.isPresent() && userOptional.get().getVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                        .message("Email already exists")
                        .build());
            }
        }
        if(repository.existsByMobile(request.getMobile()) && request.getMobile()!=null && !request.getMobile().isEmpty()){
            Optional<User> userOptional = repository.findByMobileAndVerified(request.getMobile(),true);

            if (userOptional.isPresent() && userOptional.get().getVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                        .message("Mobile already exists")
                        .build());
            }
        }

//        if (request.getUsername().length() >= 15 || request.getUsername().length() < 5) {
//            return ResponseMessage.builder()
//                    .message("Username must be less than 15 characters and greater than 5")
//                    .build();
//        }
//        if (request.getUsername().contains(" ")) {
//            return ResponseMessage.builder()
//                    .message("Username must not contain spaces")
//                    .build();
//        }
        if(request.getFirstName().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("First name is required")
                    .build());
        }
        if(request.getFirstName().length()>20){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("First name can not be longer than 20 characters")
                    .build());
        }
        if(request.getLastName()!=null){
        if(request.getLastName().length()>20){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Last name can not be longer than 20 characters")
                    .build());
        }}

        if(request.getEmail()==null && request.getMobile().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Either Email or Mobile number is required")
                    .build());
        }

        String password = request.getPassword();
        if (password.length() < 8 || password.length()>20) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must greater than 8 characters and less than 20 characters")
                    .build());
        }
        if (!password.matches(".*[A-Z].*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must contain at least one uppercase letter")
                    .build());
        }
        if (!password.matches(".*[a-z].*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must contain at least one lowercase letter")
                    .build());
        }
        if (!password.matches(".*\\d.*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must contain at least one digit")
                    .build());
        }
        if (!password.matches(".*[!@#$%^&*()\\-_=+{};:,<.>].*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must contain at least one special character")
                    .build());
        }
        if (password.contains(" ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must not contain spaces")
                    .build());
        }
        Role role = request.getRole() != null ? request.getRole() : Role.CANDIDATE;
        String contact=request.getEmail()!=null ? request.getEmail() : request.getMobile();
        if(repository.existsByUsernameAndVerified(contact,false)){
            var user=repository.findByUsername(contact).orElseThrow();
            long minuteElapsed = user.getRegisterRequestTimer() != null
                    ? ChronoUnit.SECONDS.between(user.getRegisterRequestTimer(), LocalDateTime.now())
                    : 60;
            if(minuteElapsed<30){return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Can't send request before 30 seconds")
                    .build());}


            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setMobile(request.getMobile());
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setVerified(false);
            String otp= generateotp();
            user.setOtp(otp);
            user.setOtpGenerated(LocalDateTime.now());
            user. setRegisterRequestTimer(LocalDateTime.now());
            repository.save(user);
            if(user.getEmail()!=null){
                return (sendVerificationEmail(user.getEmail(), otp));
            }
            else{
                return (twilioService.sendOtp(request.getMobile(), otp));

            }
        }
        else{var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .username(contact)
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .verified(false)
                .registerRequestTimer(LocalDateTime.now())
                .build();


        String otp= generateotp();
        user.setOtp(otp);
        user.setOtpGenerated(LocalDateTime.now());
        repository.save(user);
        if(user.getEmail()!=null){
            return (sendVerificationEmail(user.getEmail(), otp));
        }
        else{
            return (twilioService.sendOtp(request.getMobile(), otp));

        }}

    }

    public ResponseEntity authenticate(AuthenticationRequest request) {
//        if (request.getUs().length() >= 15 || request.getUsername().length() < 5) {
//            return  AuthenticationResponse.builder()
//                    .token(null)
//                    .message("Username must be less than 15 characters and greater than 5")
//                    .build();
//        }
        if(!repository.existsByUsername(request.getContact())){ return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthenticationResponse.builder()
                .message("User does not exist")
                .build());}
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getContact(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthenticationResponse.builder()
                    .message("Incorrect password")
                    .build());
        }

        var user = repository.findByUsername(request.getContact()).orElseThrow();
        user.setVerified(true);
        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Login successful")
                .build());

    }
    private String generateotp(){
        Random random=new Random();
       int otpvalue= 100000+random.nextInt(900000);
       return String.valueOf(otpvalue);
    }
    public ResponseEntity sendVerificationEmail(String email, String otp) {
        String subject = "Verification Mail";
        String imageUrl = "https://i.ibb.co/kJkpyt6/Workify.png";
        String body = "<html><body>" +
                "<img src='" + imageUrl + "' alt='Verification Image' style='max-width:100%;height:auto;'>" +
                "<p>Your verification code is <strong>" + otp + "</strong></p>" +
                "</body></html>";

        // Set the content type to HTML
        return emailService.sendEmail(email, subject, body, true);


    }
    public AuthenticationResponse generateToken(User user) {
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public ResponseEntity validate(OtpValidate request) {
//        if (request.getUsername().length() >= 15 || request.getUsername().length() < 5) {
//            return  AuthenticationResponse.builder()
//                    .message("Username must be less than 15 characters and greater than 5")
//                    .build();
//        }
//        if(repository.existsByUsername(request.getUsername())){
//            Optional<User> userOptional = repository.findByUsername(request.getUsername());
//
//            if (userOptional.isPresent() && userOptional.get().getVerified()) {
//                return AuthenticationResponse.builder()
//                        .message("Username already exists")
//                        .build();
//            }
//
//        }
        if(!repository.existsByUsername(request.getContact())){return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthenticationResponse.builder()
                .message("Invalid Contact")
                .build());}
        var user=repository.findByUsername(request.getContact()).orElseThrow();
        long minuteElapsed = ChronoUnit.MINUTES.between(user.getOtpGenerated(), LocalDateTime.now());
        if(user.getOtp().equals(request.getOtp()) && minuteElapsed < 5){
            user.setVerified(true);
            repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .message("Account has been registered successfully")
                    .token(jwtToken)
                    .build());
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(AuthenticationResponse.builder()
                    .message("invalid OTP")
                    .build());
        }

    }

    public ResponseEntity forgotPassword(String contact)  {
//        if (username.length() >= 15 || username.length() < 5) {
//            return ResponseMessage.builder()
//                    .message("Username should be greater than 5 characters and less than 15")
//                    .build();
//        }
        if(!repository.existsByUsername(contact)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Contact not registered")
                    .build());
        }

        var user = repository.findByUsername(contact).orElseThrow();
        long minuteElapsed = user.getResendOtpTimer() != null
                ? ChronoUnit.SECONDS.between(user.getResendOtpTimer(), LocalDateTime.now())
                :60;

        if(minuteElapsed<30){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("OTP can not be send before 30 second")
                    .build());
        }
        String otp= generateotp();
        user.setOtp(otp);
        user.setOtpGenerated(LocalDateTime.now());
        user.setResendOtpTimer(LocalDateTime.now());
        repository.save(user);
        if(user.getEmail()!=null){
            return (sendVerificationEmail(user.getEmail(), otp));
        }
        else{
            return (twilioService.sendOtp(user.getMobile(), otp));

        }
    }

    public ResponseEntity verifyForgotPassword(ValidateForgotPasswordRequest request) {
//        if (request.getUsername().length() >= 15 || request.getUsername().length() < 5) {
//            return ResponseMessage.builder()
//                    .message("Username must be less than 15 characters and greater than 5")
//                    .build();
//        }
        String password = request.getNewPassword();
        if (password.length() < 8 || password.length()>20) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must greater than 8 characters and less than 20 characters")
                    .build());
        }
        if (!password.matches(".*[A-Z].*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must contain at least one uppercase letter")
                    .build());
        }
        if (!password.matches(".*[a-z].*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must contain at least one lowercase letter")
                    .build());
        }
        if (!password.matches(".*\\d.*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must contain at least one digit")
                    .build());
        }
        if (!password.matches(".*[!@#$%^&*()\\-_=+{};:,<.>].*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must contain at least one special character")
                    .build());
        }
        if (password.contains(" ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Password must not contain spaces")
                    .build());
        }

        var user=repository.findByUsername(request.getContact()).orElseThrow();

        if( request.getNewPassword().equals(request.getConfirmPassword())&& user.getChangepassOTP()==true    ){
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setChangepassOTP(false);
            repository.save(user);
            return ResponseEntity.ok(ResponseMessage.builder()
                    .message("Password changed successfully")
                    .build());
        }
        else {

             if( !user.getChangepassOTP()==true){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                        .message("Otp not verified")
                        .build());
            }
            else if(!request.getNewPassword().equals(request.getConfirmPassword()))
            { return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Confirm Password not same as New password")
                    .build());}
            else{
                 return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                         .message("Otp timeout")
                         .build());
             }

        }
    }

    public ResponseEntity<ResponseMessage> verifyOtpForgotPassword(OtpValidate request) {
        if(!repository.existsByUsername(request.getContact())){return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                .message("Invalid Contact")
                .build());}
        var user=repository.findByUsername(request.getContact()).orElseThrow();
        long minuteElapsed = ChronoUnit.MINUTES.between(user.getResendOtpTimer(), LocalDateTime.now());
        if(user.getOtp().equals(request.getOtp()) && minuteElapsed < 5){
            user.setChangepassOTP(true);
            repository.save(user);
            return ResponseEntity.ok(ResponseMessage.builder()
                    .message("valid OTP")
                    .build());
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("invalid OTP")
                    .build());
        }

    }
}

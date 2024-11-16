package com.workify.auth.service;

import com.workify.auth.models.dto.ResponseMessage;
import jakarta.mail.internet.MimeMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender){
        this.mailSender=mailSender;
    }
    public ResponseEntity sendEmail(String to, String subject, String body,Boolean ishtml) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body,ishtml);
            mailSender.send(message);
            // Return a successful response if email is sent

            return ResponseEntity.ok(ResponseMessage.builder()
                    .message("Email sent successfully to " + to)
                    .build());
        } catch (Exception e) {
            // Handle any other exceptions
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                    .message("Either invalid Email or connection issue")
                    .build());
        }
    }}

package com.workify.auth.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.workify.auth.models.ResponseMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    public ResponseEntity sendOtp(String toPhoneNumber, String otp) {
        String messageBody = "Your OTP code is: " + otp;

       try{ Message message = Message.creator(
                new com.twilio.type.PhoneNumber(toPhoneNumber),
                new com.twilio.type.PhoneNumber(fromPhoneNumber),
                messageBody
        ).create();
           return ResponseEntity.ok(ResponseMessage.builder()
                   .message("OTP sent successfully to "+ toPhoneNumber )
                   .build());}
       catch (Exception e){
           System.err.println("An unexpected error occurred: " + e.getMessage());
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseMessage.builder()
                   .message("Incorrect mobile number or bad network" )
                   .build());}
       }
    }


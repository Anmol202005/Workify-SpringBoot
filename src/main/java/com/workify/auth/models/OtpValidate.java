package com.workify.auth.models;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpValidate {
    private String username;
    private String otp;
}

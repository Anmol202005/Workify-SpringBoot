package com.workify.auth.models.dto;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpValidate {
    private String contact;
    private String otp;
}

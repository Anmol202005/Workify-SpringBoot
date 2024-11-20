package com.workify.auth.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GetResponseRecruiter {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String companyEmail;
    private String companyName;
    private String jobTitle;
    private String companyWebsite;
    private String companyLocation;
    private String industry;
    private byte[] profileImage;
}

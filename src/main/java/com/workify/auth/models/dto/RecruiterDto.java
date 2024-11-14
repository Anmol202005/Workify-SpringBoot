package com.workify.auth.models.dto;

import com.workify.auth.models.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RecruiterDto {

    private String companyEmail;
    private String companyName;
    private String jobTitle;
    private String companyWebsite;
    private String companyLocation;
    private String industry;
}

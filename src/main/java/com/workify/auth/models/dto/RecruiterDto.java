package com.workify.auth.models.dto;

import com.workify.auth.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RecruiterDto {

    @Email(message = "invalid Email")
    private String companyEmail;
    private String companyName;
    private String jobTitle;

    @Pattern(
            regexp = "^(https?:\\/\\/)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})?(\\/.*)?$",
            message = "Invalid website URL format"
    )
    private String companyWebsite;
    private String companyLocation;
    private String industry;
    private byte[] profileImage;
}

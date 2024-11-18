package com.workify.auth.models.dto;

import com.workify.auth.models.Certificate;
import com.workify.auth.models.Education;
import com.workify.auth.models.Experience;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
@Getter
@Setter
@ToString
public class GetResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private List<Education> education;
    private List<Experience> experience;
    private String skill;
    private List<Certificate> certificate;
    private String resumeKey;
    private String profileImageKey;
}

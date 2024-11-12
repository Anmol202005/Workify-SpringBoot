package com.workify.auth.models.dto;

import lombok.Data;

@Data
public class JobDto {
    private String title;
    private String description;
    private String location;
    private String industry;
    private String employmentType;
}
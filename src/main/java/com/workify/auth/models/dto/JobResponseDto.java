package com.workify.auth.models.dto;

import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JobResponseDto {
    private String title;
    private String description;
    private String location;
    private Integer Experience;
    private Integer minSalary;
    private Integer maxSalary;
    private String employmentType;
    private List<String> requiredSkills;
}

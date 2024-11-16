package com.workify.auth.models.dto;

import jakarta.persistence.ElementCollection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Data
@Getter
@Setter
@ToString
public class JobDto {
    private String title;
    private String description;
    private Integer minSalary;
    private Integer maxSalary;
    private String employmentType;
    @ElementCollection
    private List<String> requiredSkills;
}
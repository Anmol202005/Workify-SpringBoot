package com.workify.auth.models.dto;

import com.workify.auth.models.JobStatus;
import com.workify.auth.models.JobType;
import com.workify.auth.models.Mode;
import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JobResponseDto {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Integer Experience;
    private Integer minSalary;
    private Integer maxSalary;
    private Mode mode;
    private JobType jobType;
    private JobStatus jobStatus;
    private List<String> requiredSkills;
}

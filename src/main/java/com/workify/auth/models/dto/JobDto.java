package com.workify.auth.models.dto;

import com.workify.auth.models.JobStatus;
import com.workify.auth.models.JobType;
import com.workify.auth.models.Mode;
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
    private String location;
    private Integer Experience;
    private Integer minSalary;
    private Integer maxSalary;

    @ElementCollection
    private List<String> requiredSkills;
    private String jobType;
    private String mode;
    private String jobStatus;

}
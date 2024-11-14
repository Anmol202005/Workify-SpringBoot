package com.workify.auth.models.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class JobDto {
    private String title;
    private String description;
    private String location;
    private String industry;
    private String employmentType;
}
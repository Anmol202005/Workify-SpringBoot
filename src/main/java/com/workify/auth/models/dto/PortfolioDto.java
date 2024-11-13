package com.workify.auth.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PortfolioDto {
    private String title;
    private String description;
    private String website;
    private String github;
    private String linkedin;
}
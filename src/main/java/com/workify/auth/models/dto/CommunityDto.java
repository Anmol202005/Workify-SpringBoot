package com.workify.auth.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommunityDto {
    private String name;
    private String description;
    private String createdBy;
}

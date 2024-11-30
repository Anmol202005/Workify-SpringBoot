package com.workify.auth.models.dto;

import com.workify.auth.models.Certificate;
import com.workify.auth.models.Education;
import com.workify.auth.models.Experience;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class CandidateDTO {
    private List<Education> education;
    private List<Experience> experience;
    private List<String> skill;
    private LocalDate DOB;
    private String domain;
    private String location;
}

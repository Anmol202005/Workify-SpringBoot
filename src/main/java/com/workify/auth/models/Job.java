package com.workify.auth.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String company;
    private String location;
    private Integer experience;
    private String industry;
    private String employmentType;
    private LocalDateTime postedAt;
    private Integer minSalary;
    private Integer maxSalary;
    @ElementCollection
    private List<String> requiredSkills;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Recruiter postedBy;


}

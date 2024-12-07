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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "short_id_seq")
    @SequenceGenerator(name = "short_id_seq", sequenceName = "short_id_seq", allocationSize = 1, initialValue = 1000)
    private Long id;


    private String title;
    private String description;
    private String company;
    private String location;
    private Integer experience;
    private String industry;
    private LocalDateTime postedAt;
    private JobType jobType;
    private Mode mode;
    private Integer minSalary;
    private Integer maxSalary;
    @ElementCollection
    private List<String> requiredSkills;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Recruiter postedBy;
    private JobStatus jobStatus;


}

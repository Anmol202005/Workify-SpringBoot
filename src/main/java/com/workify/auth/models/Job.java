package com.workify.auth.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    private Recruiter recruiter;

    private String title;
    private String description;
    private String location;
    private String industry;
    private String employmentType;
}
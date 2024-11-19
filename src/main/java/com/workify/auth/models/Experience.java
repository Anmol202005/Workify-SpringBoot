package com.workify.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Entity
@Table(name = "candidate_experience")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private Integer yearsWorked;
    private String position;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // Constructors, getters, and setters
}

package com.workify.auth.models;


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
@Table(name = "candidate_certificate")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String certificateName;
    private String fileKey;



    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;


}

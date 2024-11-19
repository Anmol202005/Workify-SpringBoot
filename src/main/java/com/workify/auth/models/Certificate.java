package com.workify.auth.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.net.URL;

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
    private URL fileKey;



    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;


}

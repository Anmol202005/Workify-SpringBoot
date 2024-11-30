package com.workify.auth.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User user;
    @ElementCollection
    private List<String> skills;
    private LocalDate DOB;
    private String domain;
    private URL resumeKey;
    private URL portfolioKey;
    private URL profileImageKey;
    private String location;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "candidate")
    @JsonIgnore
    private List<Experience> experiences;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "candidate")
    @JsonIgnore
    private List<Education> education;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "candidate")
    @JsonIgnore
    private List<Certificate> certificates;


}

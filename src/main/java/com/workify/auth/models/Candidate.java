package com.workify.auth.models;

import jakarta.persistence.*;
import lombok.*;

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
    private Integer id;


    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;


    private String skills;

    @Lob
    private byte[] resume;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "candidate")
    private List<Experience> experiences;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "candidate")
    private List<Education> educationList;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "candidate")
    private List<Certificate> certificates;


}

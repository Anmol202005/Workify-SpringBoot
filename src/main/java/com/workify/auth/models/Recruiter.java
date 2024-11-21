package com.workify.auth.models;


import jakarta.persistence.*;
import lombok.*;

import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    private String companyEmail;
    private String companyName;
    private String jobTitle;
    private String companyWebsite;
    private String companyLocation;
    private String industry;
    private URL profileImage;
}

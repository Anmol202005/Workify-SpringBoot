package com.workify.auth.models.community;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String createdBy;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    private List<ChatMessage> messages;

    @ElementCollection
    private Set<String> members = new HashSet<>();

}


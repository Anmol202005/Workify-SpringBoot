package com.workify.auth.models.community;

import com.workify.auth.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime creationDateTime;
    private LocalDateTime updateDateTime;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    private List<ChatMessage> messages;

    @ElementCollection
    private Set<Long> members = new HashSet<>(); //(contain use id)

}


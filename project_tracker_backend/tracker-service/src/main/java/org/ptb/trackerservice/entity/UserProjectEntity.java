package org.ptb.trackerservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_project")
public class UserProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_project_id")
    private Integer userProjectId;

    // DECOUPLED: The remote User ID from Auth-Service
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @Column(nullable = false)
    private String role;
}

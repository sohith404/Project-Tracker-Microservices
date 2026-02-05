package org.ptb.trackerservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "project")
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // DECOUPLED: No more UserEntity. We store only the ID from Auth-Service.
    @Column(name = "created_by_user_id", nullable = false)
    private Integer createdByUserId;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Stays as is: Internal relationship
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TaskEntity> tasks;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
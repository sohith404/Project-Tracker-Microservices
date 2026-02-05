package org.ptb.trackerservice.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.ptb.trackerservice.dto.UserDTO;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Data
@Table(name = "task")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "priority")
    private String priority;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private TaskStatusEntity status;

    // DECOUPLED IDs
    @Column(name = "assigned_to_user_id")
    private Integer assignedToUserId;

    @Column(name = "created_by_user_id", nullable = false)
    private Integer createdByUserId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("assignedTo")
    private void unpackNestedUser(Map<String, Object> assignedTo) {
        if (assignedTo != null && assignedTo.containsKey("userId")) {
            this.assignedToUserId = (Integer) assignedTo.get("userId");
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

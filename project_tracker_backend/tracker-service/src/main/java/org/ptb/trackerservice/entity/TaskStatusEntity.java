package org.ptb.trackerservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "task_status")
public class TaskStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @Column(nullable = false, unique = true)
    private String name;
}

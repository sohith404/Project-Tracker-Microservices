package org.ptb.trackerservice.repository;


import org.ptb.trackerservice.entity.TaskStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, Integer> {
    Optional<TaskStatusEntity> findByName(String name);
}

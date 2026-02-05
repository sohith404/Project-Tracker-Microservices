package org.ptb.trackerservice.repository;

import org.ptb.trackerservice.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {

    List<TaskEntity> findByProjectProjectId(Integer projectId);

    // Matches the renamed field: assignedToUserId
    List<TaskEntity> findByAssignedToUserId(Integer userId);

    List<TaskEntity> findByProjectProjectIdAndStatusName(Integer projectId, String statusName);
}

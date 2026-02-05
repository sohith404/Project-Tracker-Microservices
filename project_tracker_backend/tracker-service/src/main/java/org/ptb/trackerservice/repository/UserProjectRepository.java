package org.ptb.trackerservice.repository;

import org.ptb.trackerservice.entity.UserProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProjectEntity, Integer> {

    List<UserProjectEntity> findByProjectProjectId(Integer projectId);

    // REWRITTEN: Using the flat userId field
    Optional<UserProjectEntity> findByUserIdAndProjectProjectId(Integer userId, Integer projectId);
}
package org.ptb.trackerservice.repository;


import org.ptb.trackerservice.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {

    // REWRITTEN: We join UserProjectEntity but filter on the simple userId field
    @Query("SELECT DISTINCT p FROM ProjectEntity p " +
            "LEFT JOIN UserProjectEntity up ON p.projectId = up.project.projectId " +
            "WHERE p.createdByUserId = :userId " +
            "OR up.userId = :userId")
    List<ProjectEntity> findProjectsForUser(@Param("userId") Integer userId);

    // Matches the new field name in ProjectEntity
    List<ProjectEntity> findByCreatedByUserId(Integer userId);
}

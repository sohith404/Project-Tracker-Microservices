package org.ptb.trackerservice.controller;

import org.ptb.trackerservice.entity.ProjectEntity;
import org.ptb.trackerservice.entity.TaskEntity;
import org.ptb.trackerservice.service.ProjectService;
import org.ptb.trackerservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    // Notice: NO UserService here. We use UserClient in the Service layer instead.

    @PostMapping
    public ResponseEntity<ProjectEntity> createProject(
            @RequestBody ProjectEntity project,
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(projectService.createProject(project, email));
    }

    @GetMapping
    public ResponseEntity<List<ProjectEntity>> getMyProjects(
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(projectService.getProjectsForUser(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectEntity> updateProject(
            @PathVariable Integer id,
            @RequestBody ProjectEntity projectDetails,
            @RequestHeader("X-User-Email") String email) {
        //logger.info("==> [DEBUG] Update request for Project ID: {} by User: {}", id, email);
        ProjectEntity updatedProject = projectService.updateProject(id, projectDetails, email);
        return ResponseEntity.ok(updatedProject);
    }

    // Standard CRUD remains mostly the same
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectEntity> getProjectById(@PathVariable Integer projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<TaskEntity> createTask(
            @PathVariable Integer projectId,
            @RequestBody TaskEntity task,
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(taskService.createTask(projectId, task, email));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Integer projectId,
            @RequestHeader("X-User-Email") String email) {
        projectService.deleteProject(projectId, email);
        return ResponseEntity.ok().build();
    }
}
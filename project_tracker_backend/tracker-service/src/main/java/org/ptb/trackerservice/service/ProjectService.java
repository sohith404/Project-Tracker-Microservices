package org.ptb.trackerservice.service;

import org.ptb.trackerservice.client.UserClient;
import org.ptb.trackerservice.dto.UserDTO;
import org.ptb.trackerservice.entity.ProjectEntity;
import org.ptb.trackerservice.entity.UserProjectEntity;
import org.ptb.trackerservice.repository.ProjectRepository;
import org.ptb.trackerservice.repository.UserProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ProjectService {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserClient userClient; // Feign Client
    @Autowired private UserProjectRepository userProjectRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    public List<ProjectEntity> getProjectsForUser(String email) {
        // Fetch user metadata from Auth-Service over the network
        UserDTO user = userClient.getUserByEmail(email);

        if ("ADMIN".equals(user.getRole())) {
            return projectRepository.findAll();
        }

        // Logic uses the ID returned from the Auth-Service
        return projectRepository.findProjectsForUser(user.getUserId());
    }

    public ProjectEntity createProject(ProjectEntity project, String email) {
        logger.info("==> [DEBUG] Entering createProject for user: {}", email);
        if (project == null) {
            logger.error("==> [DEBUG] Project object passed to createProject is NULL");
            throw new RuntimeException("Project data is missing");
        }
        logger.info("==> [DEBUG] Attempting Feign call to Auth-Service for email: {}", email);
        UserDTO user;
        try {
            user = userClient.getUserByEmail(email);

            if (user == null || user.getUserId() == null) {
                logger.error("==> [DEBUG] Auth-Service returned an empty UserDTO for email: {}", email);
                throw new RuntimeException("User not found in Auth-Service");
            }

            logger.info("==> [DEBUG] Feign Success: Found User ID {} with Name {}", user.getUserId(), user.getName());
        } catch (Exception e) {
            logger.error("==> [DEBUG] Feign call to getUserByEmail FAILED. Error: {}", e.getMessage());
            // This re-throw ensures the error isn't swallowed
            throw new RuntimeException("External Auth-Service failure: " + e.getMessage());
        }

        // 3. Mapping and Saving
        try {
            project.setCreatedByUserId(user.getUserId());
            logger.info("==> [DEBUG] Saving project '{}' to local Tracker DB...", project.getName());

            ProjectEntity savedProject = projectRepository.save(project);
            logger.info("==> [DEBUG] Project successfully saved with ID: {}", savedProject.getProjectId());

            return savedProject;
        } catch (Exception e) {
            logger.error("==> [DEBUG] Database save FAILED for project. Error: {}", e.getMessage());
            throw e;
        }
    }

    public ProjectEntity updateProject(Integer id, ProjectEntity projectDetails, String email) {
        // 1. Find existing project
        ProjectEntity existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        // 3. Update ONLY specific fields
        if (projectDetails.getName() != null) {
            existingProject.setName(projectDetails.getName());
        }
        if (projectDetails.getDescription() != null) {
            existingProject.setDescription(projectDetails.getDescription());
        }
        logger.info("==> [DEBUG] Saving updated project: {}", existingProject.getName());
        return projectRepository.save(existingProject);
    }

    public void addMember(Integer projectId, Integer userId) {
        // Ensure project exists locally
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Validate user exists in Auth-Service via Feign before adding membership
        try {
            userClient.getUserById(userId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot add member: User " + userId + " does not exist in Auth-Service");
        }

        if (userProjectRepository.findByUserIdAndProjectProjectId(userId, projectId).isPresent()) {
            throw new RuntimeException("User is already a member");
        }

        UserProjectEntity membership = new UserProjectEntity();
        membership.setUserId(userId);
        membership.setProject(project);
        membership.setRole("MEMBER");

        userProjectRepository.save(membership);
    }

    public ProjectEntity getProjectById(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
    }

    public void deleteProject(Integer projectId) {
        projectRepository.deleteById(projectId);
    }
}

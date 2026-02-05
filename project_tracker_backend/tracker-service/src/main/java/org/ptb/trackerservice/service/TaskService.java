package org.ptb.trackerservice.service;


import org.ptb.trackerservice.client.UserClient;
import org.ptb.trackerservice.dto.UserDTO;
import org.ptb.trackerservice.entity.ProjectEntity;
import org.ptb.trackerservice.entity.TaskEntity;
import org.ptb.trackerservice.entity.TaskStatusEntity;
import org.ptb.trackerservice.repository.ProjectRepository;
import org.ptb.trackerservice.repository.TaskRepository;
import org.ptb.trackerservice.repository.TaskStatusRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserClient userClient;
    @Autowired private TaskStatusRepository taskStatusRepository;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskEntity createTask(Integer projectId, TaskEntity task, String email) {
        logger.info("DEBUG: Entering createTask for ProjectID: {} and UserEmail: {}", projectId, email);
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    logger.error("DEBUG: Project with ID {} not found in Tracker DB", projectId);
                    return new RuntimeException("Project not found");
                });
        logger.info("DEBUG: Project found: {}", project.getProjectId());
        logger.info("DEBUG: Attempting Feign call to Auth-Service for email: {}", email);
        UserDTO creator;
        try {
            creator = userClient.getUserByEmail(email);
            logger.info("DEBUG: Feign call successful. Received UserID: {} with Role: {}",
                    creator.getUserId(), creator.getRole());
        } catch (Exception e) {
            logger.error("DEBUG: Feign call to getUserByEmail FAILED. Error: {}", e.getMessage());
            throw new RuntimeException("Auth-Service communication failed: " + e.getMessage());
        }
        if (task.getStatus() == null) {
            logger.warn("DEBUG: Task status was null, setting to default (1)");
            TaskStatusEntity defaultStatus = taskStatusRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Default status 1 not found"));
            task.setStatus(defaultStatus);
        }
        task.setProject(project);
        task.setCreatedByUserId(creator.getUserId());
        if (task.getAssignedToUserId() != null) {
            logger.info("DEBUG: Validating Assignee ID: {} via Feign", task.getAssignedToUserId());
            try {
                userClient.getUserById(task.getAssignedToUserId());
                logger.info("DEBUG: Assignee validation successful");
            } catch (Exception e) {
                logger.error("DEBUG: Assignee validation FAILED for ID: {}. Error: {}",
                        task.getAssignedToUserId(), e.getMessage());
                throw new RuntimeException("Invalid Assignee: " + e.getMessage());
            }
        }

        TaskEntity savedTask = taskRepository.save(task);
        logger.info("DEBUG: Task saved successfully with ID: {}", savedTask.getTaskId());
        return savedTask;
    }




    public List<TaskEntity> getTasksAssignedToUser(String email) {
        UserDTO user = userClient.getUserByEmail(email);
        List<TaskEntity> tasks=taskRepository.findByAssignedToUserId(user.getUserId());
        if(tasks==null){
            return Collections.emptyList();
        }
        return tasks;
    }



    public TaskEntity unassignUserFromTask(Integer taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO currentUser = userClient.getUserByEmail(currentUserName);
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            throw new RuntimeException("Access Denied: Admin only.");
        }
        task.setAssignedToUserId(null);
        return taskRepository.save(task);
    }

    public TaskEntity updateTaskStatus(Integer taskId, Integer statusId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        TaskStatusEntity newStatus = taskStatusRepository.findById(statusId)
                .orElseThrow(() -> new RuntimeException("Status ID " + statusId + " is invalid."));

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public TaskEntity updateTaskDetails(Integer taskId, TaskEntity taskUpdates) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Update basic fields
        if (taskUpdates.getTitle() != null) task.setTitle(taskUpdates.getTitle());
        if (taskUpdates.getDescription() != null) task.setDescription(taskUpdates.getDescription());
        if (taskUpdates.getPriority() != null) task.setPriority(taskUpdates.getPriority());
        if (taskUpdates.getDueDate() != null) task.setDueDate(taskUpdates.getDueDate());

        // Update Assignment with Remote Validation
        if (taskUpdates.getAssignedToUserId() != null) {
            try {
                // Network call to Auth-Service
                userClient.getUserById(taskUpdates.getAssignedToUserId());
                task.setAssignedToUserId(taskUpdates.getAssignedToUserId());
            } catch (Exception e) {
                throw new RuntimeException("Assignee update failed: User does not exist in Auth-Service.");
            }
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Integer taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO currentUser = userClient.getUserByEmail(currentEmail);

        // Security check using logical IDs
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        boolean isCreator = task.getCreatedByUserId().equals(currentUser.getUserId());

        if (isAdmin || isCreator) {
            taskRepository.delete(task);
        } else {
            throw new RuntimeException("Access Denied.");
        }
    }
}

package org.ptb.trackerservice.controller;

import org.ptb.trackerservice.entity.TaskEntity;
import org.ptb.trackerservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // The Gateway should extract the user email from the JWT and put it in a header
    @GetMapping("/assigned")
    public ResponseEntity<List<TaskEntity>> getMyAssignedTasks(
            @RequestHeader("X-User-Email") String email) {
        // Log it so you can prove the header is actually reaching the controller
        //logger.info("DEBUG: Fetching assigned tasks for email from header: {}", email);
        if (email == null || email.isEmpty()) {
            //logger.error("DEBUG: X-User-Email header is missing!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(taskService.getTasksAssignedToUser(email));
    }



    @PutMapping("/{taskId}/status")
    public ResponseEntity<TaskEntity> updateTaskStatus(
            @PathVariable Integer taskId,
            @RequestBody Map<String, Integer> requestBody) {

        Integer newStatusId = requestBody.get("statusId");
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, newStatusId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskEntity> updateTaskDetails(
            @PathVariable Integer taskId,
            @RequestBody TaskEntity taskUpdates) {
        return ResponseEntity.ok(taskService.updateTaskDetails(taskId, taskUpdates));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}

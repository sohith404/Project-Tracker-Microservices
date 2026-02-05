package org.ptb.authservice.controller;

import org.ptb.authservice.dto.UserDTO;
import org.ptb.authservice.entities.UserEntity;
import org.ptb.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/users")
public class InternalUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        UserEntity user = userService.getUserById(id);
        return ResponseEntity.ok(convertToDTO(user)); // FIX: Don't return empty DTO
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserEntity user = userService.getUserByEmail(email);
        return ResponseEntity.ok(convertToDTO(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        //logger.info("DEBUG: Auth-Service - Fetching all users for internal list request");
        List<UserEntity> users = userService.getAllUsers();
        // Map the entities to DTOs to avoid leaking passwords or sensitive data
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .toList();
        return ResponseEntity.ok(userDTOs);
    }

    private UserDTO convertToDTO(UserEntity user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
}

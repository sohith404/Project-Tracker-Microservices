package org.ptb.authservice.controller;

import org.ptb.authservice.entities.UserEntity;
import org.ptb.authservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    // This stays here because Auth-Service owns the User data
    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}

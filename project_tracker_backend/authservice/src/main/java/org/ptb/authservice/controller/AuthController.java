package org.ptb.authservice.controller;

import org.ptb.authservice.dto.AuthResponse;
import org.ptb.authservice.dto.LoginRequest;
import org.ptb.authservice.dto.SignupRequest;
import org.ptb.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        try {
            String responseMessage = authService.registerUser(signUpRequest);
            return ResponseEntity.ok(responseMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.loginUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}
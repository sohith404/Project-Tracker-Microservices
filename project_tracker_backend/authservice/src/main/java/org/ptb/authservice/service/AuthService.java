package org.ptb.authservice.service;

import org.ptb.authservice.dto.AuthResponse;
import org.ptb.authservice.dto.LoginRequest;
import org.ptb.authservice.dto.SignupRequest;
import org.ptb.authservice.entities.UserEntity;
import org.ptb.authservice.repository.UserRepository;
import org.ptb.authservice.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;


    public String registerUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole("USER");

        userRepository.save(user);
        return "User registered successfully!";
    }

    public AuthResponse loginUser(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity userDetails = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        String jwt = jwtUtils.generateJwtToken(authentication, userDetails.getRole());

        return new AuthResponse(
                jwt,
                userDetails.getEmail(),
                userDetails.getName(),
                userDetails.getRole(),
                userDetails.getUserId(),
                jwtUtils.getJwtExpirationMs()
        );
    }
}

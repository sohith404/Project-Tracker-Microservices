package org.ptb.trackerservice.client;

import org.ptb.trackerservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", url = "http://localhost:8081")
public interface UserClient {

    @GetMapping("/api/auth/users/{id}") // Removed the extra /api/auth/users if the controller is already at that base
    UserDTO getUserById(@PathVariable("id") Integer id);

    @GetMapping("/api/auth/users/email/{email}")
    UserDTO getUserByEmail(@PathVariable("email") String email);
}

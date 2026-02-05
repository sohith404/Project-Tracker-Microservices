package org.ptb.trackerservice.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomFeignErrorDecoder implements ErrorDecoder {
    private static final Logger logger = LoggerFactory.getLogger(CustomFeignErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "External Service Error";
        try {
            if (response.body() != null) {
                // This captures the ACTUAL error message from Auth-Service
                message = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            logger.error("Error reading Feign error body", e);
        }

        logger.error("==> [FEIGN ERROR] Method: {} | Status: {} | Body: {}",
                methodKey, response.status(), message);

        return switch (response.status()) {
            case 400 -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request: " + message);
            case 403 -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied: Check Auth-Service SecurityConfig!");
            case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endpoint not found: Check Controller Paths!");
            default -> new Exception("Generic Feign Failure: " + message);
        };
    }
}
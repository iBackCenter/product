package com.example.productapi.controller;

import com.example.productapi.dto.HealthResponse;
import com.example.productapi.dto.RootResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Health and status endpoints")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    @GetMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "Health check", description = "Simple health check - returns UP regardless of DummyJSON status")
    public ResponseEntity<HealthResponse> health() {
        log.debug("GET /health");
        return ResponseEntity.ok(HealthResponse.simple());
    }

    @GetMapping(value = "/api/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "API health check", description = "Detailed health check with service info")
    public ResponseEntity<HealthResponse> apiHealth() {
        log.debug("GET /api/health");
        return ResponseEntity.ok(HealthResponse.up());
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Operation(summary = "Root endpoint", description = "Service information and status")
    public ResponseEntity<RootResponse> root() {
        log.debug("GET /");
        return ResponseEntity.ok(RootResponse.running());
    }
}

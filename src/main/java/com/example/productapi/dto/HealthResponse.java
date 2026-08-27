package com.example.productapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

    private String status;
    private String service;
    private String version;

    public static HealthResponse up() {
        return HealthResponse.builder()
                .status("UP")
                .service("product-api")
                .version("1.0.0")
                .build();
    }

    public static HealthResponse simple() {
        return HealthResponse.builder().status("UP").build();
    }
}

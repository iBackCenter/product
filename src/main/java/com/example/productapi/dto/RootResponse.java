package com.example.productapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RootResponse {

    private String service;
    private String version;
    private String status;
    private String message;

    public static RootResponse running() {
        return RootResponse.builder()
                .service("product-api")
                .version("1.0.0")
                .status("running")
                .message("Product API is running")
                .build();
    }
}

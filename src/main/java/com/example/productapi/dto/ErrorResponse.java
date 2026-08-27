package com.example.productapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;
    private String message;
    private String timestamp;
    private Integer status;
    private String error;

    public static ErrorResponse of(boolean success, String message, int status, String error) {
        return ErrorResponse.builder()
                .success(success)
                .message(message)
                .timestamp(Instant.now().toString())
                .status(status)
                .error(error)
                .build();
    }
}

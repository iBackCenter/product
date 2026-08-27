package com.example.productapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    private String title;

    private String description;

    private BigDecimal price;

    private String category;

    private String thumbnail;

    @JsonProperty("discountPercentage")
    private Double discountPercentage;

    private Integer stock;

    private Float rating;

    private String brand;
}

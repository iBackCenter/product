package com.example.productapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProductResponse {

    private Long id;
    private String title;
    private String description;
    private Object price;
    private Object discountPercentage;
    private Object rating;
    private Object stock;
    private String brand;
    private String category;
    private String thumbnail;
    private Object minimumOrderQuantity;
}

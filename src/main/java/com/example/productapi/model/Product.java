package com.example.productapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    private BigDecimal discountPercentage;

    private BigDecimal rating;

    private String stock;

    private String brand;

    private String category;

    private String thumbnail;

    private List<String> images;

    @JsonProperty("minimumOrderQuantity")
    private Integer minimumOrderQuantity;

    private Long weight;

    private Dimensions dimensions;

    private String warrantyInformation;

    private String shippingInformation;

    private String availabilityStatus;

    private List<Review> reviews;

    private String returnPolicy;

    private Dimensions meta;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dimensions {
        private Float width;
        private Float height;
        private Float depth;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Review {
        private Long rating;
        private String comment;
        private String date;
        private String reviewerName;
        private String reviewerEmail;
    }
}

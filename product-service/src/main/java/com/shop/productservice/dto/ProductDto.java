package com.shop.productservice.dto;

import lombok.Data;

public class ProductDto {
    @Data
    public static class CreateRequest {
        private String name;
        private int price;
        private int stock;
        private String description;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private int price;
        private int stock;
        private String description;
    }

    @Data
    public static class ProductResponse {
        private Long id;
        private String name;
        private int price;
        private int stock;
        private String description;
    }
}

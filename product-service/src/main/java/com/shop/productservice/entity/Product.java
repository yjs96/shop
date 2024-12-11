package com.shop.productservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Column(length = 1000)
    private String description;

    @Builder
    public Product(String name, int price, int stock, String description) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new RuntimeException("Not enough stock");
        }
        this.stock -= quantity;
    }
}

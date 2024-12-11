package com.shop.productservice.controller;

import com.shop.productservice.ProductService;
import com.shop.productservice.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto.ProductResponse> createProduct(@RequestBody ProductDto.CreateRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto.ProductResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto.ProductResponse> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto.ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductDto.UpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }
}

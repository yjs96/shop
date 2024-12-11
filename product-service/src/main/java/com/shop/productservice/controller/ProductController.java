package com.shop.productservice.controller;

import com.shop.productservice.service.ProductService;
import com.shop.productservice.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto.ProductResponse> createProduct(@RequestBody ProductDto.CreateRequest request) {
        log.info("상품 등록 요청: name={}, price={}", request.getName(), request.getPrice());
        try {
            ProductDto.ProductResponse response = productService.createProduct(request);
            log.info("상품 등록 성공: productId={}, name={}", response.getId(), response.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("상품 등록 실패: name={}", request.getName(), e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto.ProductResponse>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto.ProductResponse> getProduct(@PathVariable Long productId) {
        log.info("상품 조회 요청: productId={}", productId);
        try {
            ProductDto.ProductResponse response = productService.getProduct(productId);
            log.debug("상품 조회 성공: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("상품 조회 실패: productId={}", productId, e);
            throw e;
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto.ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductDto.UpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }
}

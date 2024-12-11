package com.shop.productservice.service;

import com.shop.productservice.dto.ProductDto;
import com.shop.productservice.entity.Product;
import com.shop.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductDto.ProductResponse createProduct(ProductDto.CreateRequest request) {
        log.info("상품 등록 시도 - name: {}, price: {}", request.getName(), request.getPrice());
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .description((request.getDescription()))
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("상품 등록 성공 - productId: {}", savedProduct.getId());
        return convertToDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto.ProductResponse> getAllProducts(Pageable pageable) {
        log.info("상품 목록 조회 - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        return productRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public ProductDto.ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return convertToDto(product);
    }

    @Transactional
    public ProductDto.ProductResponse updateProduct(Long productId, ProductDto.UpdateRequest request) {
        log.info("상품 수정 시도 - productId: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("상품 수정 실패 - 존재하지 않는 상품: {}", productId);
                    return new RuntimeException("Product not found");
                });

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        log.info("상품 수정 성공 - productId: {}, name: {}", productId, request.getName());

        return convertToDto(product);
    }

    private ProductDto.ProductResponse convertToDto(Product product) {
        ProductDto.ProductResponse response = new ProductDto.ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setDescription(product.getDescription());
        return response;
    }
}

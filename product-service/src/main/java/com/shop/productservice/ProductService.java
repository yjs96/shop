package com.shop.productservice;

import com.shop.productservice.dto.ProductDto;
import com.shop.productservice.entity.Product;
import com.shop.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductDto.ProductResponse createProduct(ProductDto.CreateRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .description((request.getDescription()))
                .build();

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto.ProductResponse> getAllProducts(Pageable pageable) {
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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());

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

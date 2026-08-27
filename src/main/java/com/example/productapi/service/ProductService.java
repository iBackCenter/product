package com.example.productapi.service;

import com.example.productapi.client.DummyJsonClient;
import com.example.productapi.dto.CreateProductRequest;
import com.example.productapi.dto.UpdateProductRequest;
import com.example.productapi.model.DeleteResponse;
import com.example.productapi.model.Product;
import com.example.productapi.model.ProductListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final DummyJsonClient client;

    public ProductService(DummyJsonClient client) {
        this.client = client;
    }

    public ProductListResponse getProducts(Map<String, Object> params) {
        log.info("ProductService.getProducts called with params: {}", params);
        return client.getProducts(params);
    }

    public Product getProduct(Long id) {
        log.info("ProductService.getProduct called for id: {}", id);
        return client.getProduct(id);
    }

    public ProductListResponse searchProducts(String query) {
        log.info("ProductService.searchProducts called with q: {}", query);
        return client.searchProducts(query);
    }

    public ProductListResponse getProductsByCategory(String category) {
        log.info("ProductService.getProductsByCategory called for category: {}", category);
        return client.getProductsByCategory(category);
    }

    public List<String> getCategories() {
        log.info("ProductService.getCategories called");
        return client.getCategories();
    }

    public Product createProduct(CreateProductRequest request) {
        log.info("ProductService.createProduct called for title: {}", request.getTitle());
        return client.createProduct(request);
    }

    public Product updateProduct(Long id, UpdateProductRequest request) {
        log.info("ProductService.updateProduct called for id: {}", id);
        return client.updateProduct(id, request);
    }

    public Product patchProduct(Long id, Map<String, Object> updates) {
        log.info("ProductService.patchProduct called for id: {}", id);
        return client.patchProduct(id, updates);
    }

    public DeleteResponse deleteProduct(Long id) {
        log.info("ProductService.deleteProduct called for id: {}", id);
        return client.deleteProduct(id);
    }
}

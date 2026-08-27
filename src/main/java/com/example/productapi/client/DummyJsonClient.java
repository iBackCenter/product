package com.example.productapi.client;

import com.example.productapi.dto.CreateProductRequest;
import com.example.productapi.dto.UpdateProductRequest;
import com.example.productapi.exception.DummyJsonException;
import com.example.productapi.model.AddProductResponse;
import com.example.productapi.model.DeleteResponse;
import com.example.productapi.model.Product;
import com.example.productapi.model.ProductListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class DummyJsonClient {

    private static final Logger log = LoggerFactory.getLogger(DummyJsonClient.class);

    private final RestClient restClient;

    @Value("${dummyjson.base-url:https://dummyjson.com}")
    private String baseUrl;

    public DummyJsonClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ProductListResponse getProducts(Map<String, Object> params) {
        log.info("Fetching products from upstream with params: {}", params);
        try {
            ResponseEntity<ProductListResponse> response = restClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/products");
                        params.forEach((key, value) -> {
                            if (value != null) {
                                builder.queryParam(key, value);
                            }
                        });
                        return builder.build();
                    })
                    .retrieve()
                    .toEntity(ProductListResponse.class);
            log.info("Upstream returned: total={}", response.getBody() != null ? response.getBody().getTotal() : 0);
            return response.getBody();
        } catch (Exception e) {
            log.error("Upstream getProducts failed: {}", e.getMessage());
            throw mapException(e);
        }
    }

    public Product getProduct(Long id) {
        log.info("Fetching product {} from upstream", id);
        try {
            ResponseEntity<Product> response = restClient.get()
                    .uri("/{id}", id)
                    .retrieve()
                    .toEntity(Product.class);
            log.info("Upstream returned product: id={}", id);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Product {} not found upstream: {}", id, e.getMessage());
            throw new DummyJsonException.NotFoundException("Product not found: " + id, e);
        } catch (Exception e) {
            log.error("Upstream getProduct({}) failed: {}", id, e.getMessage());
            throw mapException(e);
        }
    }

    public ProductListResponse searchProducts(String query) {
        log.info("Searching products upstream with q={}", query);
        try {
            ResponseEntity<ProductListResponse> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/products/search")
                            .queryParam("q", query)
                            .build())
                    .retrieve()
                    .toEntity(ProductListResponse.class);
            log.info("Upstream search returned: total={}", response.getBody() != null ? response.getBody().getTotal() : 0);
            return response.getBody();
        } catch (Exception e) {
            log.error("Upstream search failed: {}", e.getMessage());
            throw mapException(e);
        }
    }

    public ProductListResponse getProductsByCategory(String category) {
        log.info("Fetching products by category: {}", category);
        try {
            ResponseEntity<ProductListResponse> response = restClient.get()
                    .uri("/products/category/{category}", category)
                    .retrieve()
                    .toEntity(ProductListResponse.class);
            log.info("Upstream category {} returned: total={}", category,
                    response.getBody() != null ? response.getBody().getTotal() : 0);
            return response.getBody();
        } catch (Exception e) {
            log.error("Upstream getProductsByCategory({}) failed: {}", category, e.getMessage());
            throw mapException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public java.util.List<String> getCategories() {
        log.info("Fetching categories from upstream");
        try {
            ResponseEntity<java.util.List> response = restClient.get()
                    .uri("/products/categories")
                    .retrieve()
                    .toEntity(java.util.List.class);
            log.info("Upstream categories returned: count={}", response.getBody() != null ? response.getBody().size() : 0);
            return response.getBody();
        } catch (Exception e) {
            log.error("Upstream getCategories failed: {}", e.getMessage());
            throw mapException(e);
        }
    }

    public Product createProduct(CreateProductRequest request) {
        log.info("Creating product upstream: title={}", request.getTitle());
        try {
            ResponseEntity<Product> response = restClient.post()
                    .uri("/products/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(Product.class);
            log.info("Product created upstream: id={}", response.getBody() != null ? response.getBody().getId() : null);
            return response.getBody();
        } catch (Exception e) {
            log.error("Upstream createProduct failed: {}", e.getMessage());
            throw mapException(e);
        }
    }

    public Product updateProduct(Long id, UpdateProductRequest request) {
        log.info("Updating product {} upstream", id);
        try {
            ResponseEntity<Product> response = restClient.put()
                    .uri("/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toEntity(Product.class);
            log.info("Product {} updated upstream", id);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Product {} not found for update", id);
            throw new DummyJsonException.NotFoundException("Product not found: " + id, e);
        } catch (Exception e) {
            log.error("Upstream updateProduct({}) failed: {}", id, e.getMessage());
            throw mapException(e);
        }
    }

    public Product patchProduct(Long id, Map<String, Object> updates) {
        log.info("Patching product {} upstream", id);
        try {
            ResponseEntity<Product> response = restClient.patch()
                    .uri("/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updates)
                    .retrieve()
                    .toEntity(Product.class);
            log.info("Product {} patched upstream", id);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Product {} not found for patch", id);
            throw new DummyJsonException.NotFoundException("Product not found: " + id, e);
        } catch (Exception e) {
            log.error("Upstream patchProduct({}) failed: {}", id, e.getMessage());
            throw mapException(e);
        }
    }

    public DeleteResponse deleteProduct(Long id) {
        log.info("Deleting product {} upstream", id);
        try {
            ResponseEntity<DeleteResponse> response = restClient.delete()
                    .uri("/{id}", id)
                    .retrieve()
                    .toEntity(DeleteResponse.class);
            log.info("Product {} deleted upstream: deleted={}", id, response.getBody() != null ? response.getBody().isDeleted() : false);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Product {} not found for delete", id);
            throw new DummyJsonException.NotFoundException("Product not found: " + id, e);
        } catch (Exception e) {
            log.error("Upstream deleteProduct({}) failed: {}", id, e.getMessage());
            throw mapException(e);
        }
    }

    private DummyJsonException mapException(Exception e) {
        if (e instanceof DummyJsonException) {
            throw (DummyJsonException) e;
        }
        if (e instanceof SocketTimeoutException || e.getCause() instanceof SocketTimeoutException) {
            return new DummyJsonException.GatewayTimeoutException("Upstream timeout: " + e.getMessage(), e);
        }
        if (e instanceof ConnectException || e.getCause() instanceof ConnectException) {
            return new DummyJsonException.BadGatewayException("Upstream unreachable: " + e.getMessage(), e);
        }
        if (e instanceof HttpClientErrorException) {
            HttpClientErrorException hce = (HttpClientErrorException) e;
            return new DummyJsonException(hce.getMessage(), hce);
        }
        if (e instanceof HttpServerErrorException) {
            HttpServerErrorException hse = (HttpServerErrorException) e;
            return new DummyJsonException.GatewayTimeoutException("Upstream error: " + hse.getMessage(), hse);
        }
        return new DummyJsonException("Upstream communication failed: " + e.getMessage(), e);
    }
}

package com.example.productapi.controller;

import com.example.productapi.dto.CreateProductRequest;
import com.example.productapi.dto.UpdateProductRequest;
import com.example.productapi.model.DeleteResponse;
import com.example.productapi.model.Product;
import com.example.productapi.model.ProductListResponse;
import com.example.productapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management endpoints - proxies DummyJSON API")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Fetch paginated list of products with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "502", description = "Bad Gateway - upstream unreachable",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout - upstream timeout",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<ProductListResponse> getProducts(
            @Parameter(description = "Number of products to return") @RequestParam(required = false) Integer limit,
            @Parameter(description = "Number of products to skip") @RequestParam(required = false) Integer skip,
            @Parameter(description = "Fields to select (comma-separated)") @RequestParam(required = false) String select,
            @Parameter(description = "Field to sort by") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort order (asc/desc)") @RequestParam(required = false) String order
    ) {
        log.info("GET /api/products - limit={}, skip={}, select={}, sortBy={}, order={}",
                limit, skip, select, sortBy, order);
        Map<String, Object> params = new java.util.HashMap<>();
        if (limit != null) params.put("limit", limit);
        if (skip != null) params.put("skip", skip);
        if (select != null) params.put("select", select);
        if (sortBy != null) params.put("sortBy", sortBy);
        if (order != null) params.put("order", order);
        ProductListResponse response = productService.getProducts(params);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Fetch a single product by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "502", description = "Bad Gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public ResponseEntity<Product> getProduct(
            @Parameter(description = "Product ID", required = true) @PathVariable Long id
    ) {
        log.info("GET /api/products/{}", id);
        Product product = productService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by query string")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned"),
            @ApiResponse(responseCode = "400", description = "Missing or empty query parameter"),
            @ApiResponse(responseCode = "502", description = "Bad Gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public ResponseEntity<ProductListResponse> searchProducts(
            @Parameter(description = "Search query string", required = true) @RequestParam("q") String query
    ) {
        log.info("GET /api/products/search?q={}", query);
        if (query == null || query.trim().isEmpty()) {
            throw new com.example.productapi.exception.SearchParameterException("Query parameter 'q' must not be empty");
        }
        ProductListResponse response = productService.searchProducts(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get products by category", description = "Fetch products filtered by category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products found for category"),
            @ApiResponse(responseCode = "502", description = "Bad Gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public ResponseEntity<ProductListResponse> getProductsByCategory(
            @Parameter(description = "Category name", required = true) @PathVariable String category
    ) {
        log.info("GET /api/products/category/{}", category);
        ProductListResponse response = productService.getProductsByCategory(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Fetch list of all available product categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved"),
            @ApiResponse(responseCode = "502", description = "Bad Gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public ResponseEntity<List<String>> getCategories() {
        log.info("GET /api/products/categories");
        List<String> categories = productService.getCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product (proxied to DummyJSON)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "502", description = "Bad Gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        log.info("POST /api/products - title={}", request.getTitle());
        Product created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product (full)", description = "Full update of a product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "502", description = "Bad Gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        log.info("PUT /api/products/{}", id);
        Product updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update product (partial)", description = "Partial update of a product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product patched"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "502", description = "Bad Gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public ResponseEntity<Product> patchProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @RequestBody Map<String, Object> updates
    ) {
        log.info("PATCH /api/products/{}", id);
        Product patched = productService.patchProduct(id, updates);
        return ResponseEntity.ok(patched);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "502", description = "Bad Gateway"),
            @ApiResponse(responseCode = "504", description = "Gateway Timeout")
    })
    public ResponseEntity<DeleteResponse> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id
    ) {
        log.info("DELETE /api/products/{}", id);
        DeleteResponse deleted = productService.deleteProduct(id);
        return ResponseEntity.ok(deleted);
    }
}

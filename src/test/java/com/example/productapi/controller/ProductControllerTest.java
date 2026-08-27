package com.example.productapi.controller;

import com.example.productapi.dto.CreateProductRequest;
import com.example.productapi.dto.UpdateProductRequest;
import com.example.productapi.exception.DummyJsonException;
import com.example.productapi.model.DeleteResponse;
import com.example.productapi.model.Product;
import com.example.productapi.model.ProductListResponse;
import com.example.productapi.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ProductController.class, HealthController.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void health_shouldReturn200WithStatusUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void apiHealth_shouldReturn200WithServiceInfo() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("product-api"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }

    @Test
    void root_shouldReturn200WithServiceInfo() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("product-api"))
                .andExpect(jsonPath("$.status").value("running"))
                .andExpect(jsonPath("$.version").value("1.0.0"));
    }

    @Test
    void getProducts_shouldReturn200WithProductList() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .title("Test Product")
                .price(BigDecimal.valueOf(99.99))
                .category("electronics")
                .build();

        ProductListResponse response = ProductListResponse.builder()
                .products(List.of(product))
                .total(1L)
                .skip(0L)
                .limit(10L)
                .build();

        when(productService.getProducts(any(Map.class))).thenReturn(response);

        mockMvc.perform(get("/api/products")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.products[0].id").value(1))
                .andExpect(jsonPath("$.products[0].title").value("Test Product"));
    }

    @Test
    void getProduct_shouldReturn200WhenFound() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .title("Test Product")
                .price(BigDecimal.valueOf(99.99))
                .category("electronics")
                .build();

        when(productService.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Product"));
    }

    @Test
    void getProduct_shouldReturn404WhenNotFound() throws Exception {
        when(productService.getProduct(999L))
                .thenThrow(new DummyJsonException.NotFoundException("Product not found: 999"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void searchProducts_shouldReturn200WithResults() throws Exception {
        Product product = Product.builder()
                .id(2L)
                .title("iPhone")
                .price(BigDecimal.valueOf(999.99))
                .category("smartphones")
                .build();

        ProductListResponse response = ProductListResponse.builder()
                .products(List.of(product))
                .total(1L)
                .skip(0L)
                .limit(10L)
                .build();

        when(productService.searchProducts("phone")).thenReturn(response);

        mockMvc.perform(get("/api/products/search")
                        .param("q", "phone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.products[0].title").value("iPhone"));
    }

    @Test
    void searchProducts_shouldReturn400WhenQueryEmpty() throws Exception {
        mockMvc.perform(get("/api/products/search")
                        .param("q", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void searchProducts_shouldReturn400WhenQueryMissing() throws Exception {
        mockMvc.perform(get("/api/products/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getCategories_shouldReturn200WithCategoryList() throws Exception {
        List<String> categories = Arrays.asList("smartphones", "laptops", "tablets");
        when(productService.getCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/products/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("smartphones"))
                .andExpect(jsonPath("$[1]").value("laptops"));
    }

    @Test
    void getProductsByCategory_shouldReturn200WithProducts() throws Exception {
        Product product = Product.builder()
                .id(1L)
                .title("iPhone 15")
                .category("smartphones")
                .build();

        ProductListResponse response = ProductListResponse.builder()
                .products(List.of(product))
                .total(1L)
                .build();

        when(productService.getProductsByCategory("smartphones")).thenReturn(response);

        mockMvc.perform(get("/api/products/category/smartphones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.products[0].category").value("smartphones"));
    }

    @Test
    void createProduct_shouldReturn200WithCreatedProduct() throws Exception {
        CreateProductRequest request = CreateProductRequest.builder()
                .title("New Product")
                .price(BigDecimal.valueOf(49.99))
                .category("test")
                .stock(100)
                .build();

        Product created = Product.builder()
                .id(100L)
                .title("New Product")
                .price(BigDecimal.valueOf(49.99))
                .category("test")
                .build();

        when(productService.createProduct(any(CreateProductRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("New Product"));
    }

    @Test
    void updateProduct_shouldReturn200WithUpdatedProduct() throws Exception {
        UpdateProductRequest request = UpdateProductRequest.builder()
                .title("Updated Title")
                .price(BigDecimal.valueOf(199.99))
                .build();

        Product updated = Product.builder()
                .id(1L)
                .title("Updated Title")
                .price(BigDecimal.valueOf(199.99))
                .build();

        when(productService.updateProduct(eq(1L), any(UpdateProductRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void patchProduct_shouldReturn200WithPatchedProduct() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("price", 299.99);

        Product patched = Product.builder()
                .id(1L)
                .title("Test")
                .price(BigDecimal.valueOf(299.99))
                .build();

        when(productService.patchProduct(eq(1L), any(Map.class))).thenReturn(patched);

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteProduct_shouldReturn200WithDeletedProduct() throws Exception {
        DeleteResponse deleted = DeleteResponse.builder()
                .id(1L)
                .title("Deleted Product")
                .deleted(true)
                .build();

        when(productService.deleteProduct(1L)).thenReturn(deleted);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    void getProduct_shouldReturn502WhenUpstreamUnreachable() throws Exception {
        when(productService.getProduct(1L))
                .thenThrow(new DummyJsonException.BadGatewayException("Upstream unreachable"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(502));
    }

    @Test
    void getProduct_shouldReturn504WhenUpstreamTimeout() throws Exception {
        when(productService.getProduct(1L))
                .thenThrow(new DummyJsonException.GatewayTimeoutException("Upstream timeout"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(504));
    }
}

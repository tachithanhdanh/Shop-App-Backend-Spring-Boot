package com.example.shopapp.controllers;

import com.example.shopapp.dtos.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductControllers {
    @PostMapping("") // http://localhost:8088/api/v1/products
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            // Save the product to the database
            return ResponseEntity.status(HttpStatus.CREATED).body("Product created: " + productDTO.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("") // http://localhost:8088/api/v1/products?page=1&limit=10
    public ResponseEntity<String> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        return ResponseEntity.ok(String.format("get all products with page %d and limit %d", page, limit));
    }

    @GetMapping("/{id}") // http://localhost:8088/api/v1/products/1
    public ResponseEntity<String> getProductById(
            @PathVariable("id") int productId
    ) {
        return ResponseEntity.ok(String.format("get product with id %d", productId));
    }

    @DeleteMapping("/{id}") // http://localhost:8088/api/v1/products/1
    public ResponseEntity<String> deleteProduct(
            @PathVariable("id") int productId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(String.format("delete product with id %d", productId));
    }
}

package com.example.shopapp.controllers;

import com.example.shopapp.dtos.ProductDTO;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/products")
public class ProductControllers {

    @PostMapping("") // http://localhost:8088/api/v1/products
    public ResponseEntity<?> createProduct(
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            List<MultipartFile> files = productDTO.getFiles();
            files = files == null ? new ArrayList<>(0) : files;
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    // skip empty files
                    continue;
                }
                // If the file size is greater than 10MB then return an error
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File size is too large! Max file size is 10MB");
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("Only image files are supported");
                }

                // Save files and update in DTO
                String fileName = storeFile(file); // replace with actual file name after saving

                // Save the file to the server
                // The file is saved in product_images table
            }
            // Save the product to the database
            return ResponseEntity.status(HttpStatus.CREATED).body("Product created: " + productDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        // get original filename
        String filename = file.getOriginalFilename();

        // Insert UUID to the filename to ensure that the filename is unique
        String uniqueFilename = java.util.UUID.randomUUID().toString() + "_" + filename;

        // Get the folder path that you want to save
        Path uploadDir = Paths.get("uploads");

        // Create the folder if it does not exist
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Full path to the file
        Path destionationPath = Paths.get(uploadDir.toString(), uniqueFilename);

        // Copy file into the folder
        Files.copy(file.getInputStream(), destionationPath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    @GetMapping("") // http://localhost:8088/api/v1/products?page=1&limit=10
    public ResponseEntity<String> getProducts(@RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        return ResponseEntity.ok(
                String.format("get all products with page %d and limit %d", page, limit));
    }

    @GetMapping("/{id}") // http://localhost:8088/api/v1/products/1
    public ResponseEntity<String> getProductById(@PathVariable("id") int productId) {
        return ResponseEntity.ok(String.format("get product with id %d", productId));
    }

    @DeleteMapping("/{id}") // http://localhost:8088/api/v1/products/1
    public ResponseEntity<String> deleteProduct(@PathVariable("id") int productId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(String.format("delete product with id %d", productId));
    }
}

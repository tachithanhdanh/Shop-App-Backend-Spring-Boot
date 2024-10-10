package com.example.shopapp.controllers;

import com.example.shopapp.dtos.ProductDTO;
import com.example.shopapp.dtos.ProductImageDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Product;
import com.example.shopapp.models.ProductImage;
import com.example.shopapp.responses.ProductListResponse;
import com.example.shopapp.responses.ProductResponse;
import com.example.shopapp.services.ProductService;
import com.github.javafaker.Faker;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductControllers {

    private final ProductService productService;

    @PostMapping("") // http://localhost:8088/api/v1/products
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(
                    productDTO); // Save the product to the database

            // Save the product to the database
            return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value = "/uploads/{product_id}")
    // http://localhost:8088/api/v1/products/upload
    public ResponseEntity<?> uploadImages(
            @PathVariable("product_id") Long productId,
            @RequestPart("files") List<MultipartFile> files
            // https://blogs.perficient.com/2020/07/27/requestbody-and-multipart-on-spring-boot/
    ) {
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<>(0) : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest()
                        .body("You can only upload a maximum of 5 images");
            }
            List<ProductImage> productImages = new ArrayList<>();
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
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(fileName)
                                .build()
                );
                productImages.add(productImage);

            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private String storeFile(MultipartFile file) throws IOException {
        if (!isImage(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid file format");
        }

        // get original filename
        // cleanPath is used to prevent directory traversal attacks
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

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

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("") // http://localhost:8088/api/v1/products?page=1&limit=10
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        // Create Pageable from page and limit
        // page is zero-based
        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<ProductResponse> productResponsePage = productService.getAllProducts(pageRequest);
        // get total pages
        int totalPages = productResponsePage.getTotalPages();
        // get list of products
        List<ProductResponse> productResponses = productResponsePage.getContent();
        return ResponseEntity.ok(
                ProductListResponse
                        .builder()
                        .products(productResponses)
                        .totalPages(totalPages)
                        .build()
        );
    }

    @GetMapping("/{id}") // http://localhost:8088/api/v1/products/1
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        try {
            Product existingProduct = productService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}") // http://localhost:8088/api/v1/products/1
    public ResponseEntity<?> updateProduct(
            @PathVariable("id") Long productId,
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product updatedProduct = productService.updateProduct(productId, productDTO);
            return ResponseEntity.ok("Product with id " + productId + " has been updated successfully");
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}") // http://localhost:8088/api/v1/products/1
    public ResponseEntity<String> deleteProduct(@PathVariable("id") int productId) {
        productService.deleteProduct((long) productId);
        return ResponseEntity.ok("Product with id " + productId + " has been deleted successfully");
    }

//    @PostMapping("/generateFakeProducts")
    private ResponseEntity<String> generateFakeProducts() {
        try {
            Faker faker = new Faker();
            for (int i = 0; i < 1_000_000; ++i) {
                ProductDTO productDTO = ProductDTO.builder()
                        .name(faker.commerce().productName())
                        .price((float) faker.number().numberBetween(10, 90_000_000))
                        .thumbnail("")
                        .description(faker.lorem().sentence())
                        .categoryId((long) faker.number().numberBetween(2, 5))
                        .build();
                productService.createProduct(productDTO);
            }
        } catch (DataNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
        return ResponseEntity.ok("Fake products generated");
    }
}

package com.example.shopapp.dtos;

import com.example.shopapp.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO {
    @JsonProperty("image_url")
    @Size(min = 5, max = 200, message = "Image URL must be between 5 and 200 characters")
    private String imageUrl;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product ID must be greater than 0")
    private Product product;
}

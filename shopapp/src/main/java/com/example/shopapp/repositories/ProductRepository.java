package com.example.shopapp.repositories;

import com.example.shopapp.models.Product;
import jakarta.annotation.Nonnull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // find products by category id
    List<Product> findByCategoryId(Long categoryId);

    // Check if product exists by name
    boolean existsByName(String name);

    // pagination for products
    @Nonnull
    Page<Product> findAll(@Nonnull Pageable pageable);
}

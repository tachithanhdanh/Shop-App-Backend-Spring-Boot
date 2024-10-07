package com.example.shopapp.repositories;

import com.example.shopapp.models.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find all orders by user id
    List<Order> findByUserId(Long userId);


}

package com.example.shopapp.repositories;

import com.example.shopapp.models.OrderDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>{
    List<OrderDetail> findByOrderId(Long orderId); // Jpa auto generate query
}

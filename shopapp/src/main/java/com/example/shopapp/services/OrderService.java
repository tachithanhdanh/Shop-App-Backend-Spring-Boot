package com.example.shopapp.services;

import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.responses.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFoundException;
    OrderResponse getOrderById(Long id) throws DataNotFoundException;
    OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;
    void deleteOrder(Long id);
    List<OrderResponse> findByUserId(Long userId);
}

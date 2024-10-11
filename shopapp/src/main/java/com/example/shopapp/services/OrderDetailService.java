package com.example.shopapp.services;

import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.OrderDetail;
import java.util.List;

public interface OrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException;
    OrderDetail getOrderDetailById(Long id) throws DataNotFoundException;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO)
            throws DataNotFoundException;
    void deleteById(Long id);
    List<OrderDetail> findByOrderId(Long orderId) throws DataNotFoundException;
}

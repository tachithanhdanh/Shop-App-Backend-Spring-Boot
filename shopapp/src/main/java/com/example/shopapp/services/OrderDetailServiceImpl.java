package com.example.shopapp.services;

import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Order;
import com.example.shopapp.models.OrderDetail;
import com.example.shopapp.models.Product;
import com.example.shopapp.repositories.OrderDetailRepository;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements
        OrderDetailService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
//    private final ModelMapper modelMapper;

    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO)
            throws DataNotFoundException {
        // check if order exists, if not, throw an exception
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find order with id: " + orderDetailDTO.getOrderId()));
        // check if product exists, if not, throw an exception
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + orderDetailDTO.getProductId()));
        // if yes then create an order detail and save it to the database
        // No need to use modelMapper here because the DTO is quite simple
        OrderDetail newOrderDetail = OrderDetail.builder()
                .order(existingOrder)
                .product(existingProduct)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        return orderDetailRepository.save(newOrderDetail);
    }

    @Override
    public OrderDetail getOrderDetailById(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id)
                .orElseThrow(
                        () -> new DataNotFoundException("Cannot find order detail with id: " + id));
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO)
            throws DataNotFoundException {
        // check if order detail exists, if not, throw an exception
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find order detail with id: " + id));
        // check if order exists, if not, throw an exception
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find order with id: " + orderDetailDTO.getOrderId()));
        // check if product exists, if not, throw an exception
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + orderDetailDTO.getProductId()));
        // if yes then update the order detail and save it to the database
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existingOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    public void deleteById(Long id) {
        // hard delete
        // no need to check if order detail exists because deleteById() will skip if not found
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) throws DataNotFoundException {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find order with id: " + orderId));
        return orderDetailRepository.findByOrderId(existingOrder.getId());
    }
}

package com.example.shopapp.services;

import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.Order;
import com.example.shopapp.models.OrderStatus;
import com.example.shopapp.models.User;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.UserRepository;
import com.example.shopapp.responses.OrderResponse;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        // Check if user exists, if not, throw an exception
        // if yes then create an order and save it to the database
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        // Map orderDTO to Order entity
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(modelMapper -> modelMapper.skip(Order::setId));
        // Update order from orderDTO
        Order order = modelMapper.map(orderDTO, Order.class);
        order.setUser(existingUser);
        order.setOrderDate(new Date(System.currentTimeMillis()));
        order.setStatus(OrderStatus.PENDING);
        Date shippingDate =
                orderDTO.getShippingDate() == null ? new Date(System.currentTimeMillis() + 86400000)
                        : new Date(orderDTO.getShippingDate().getTime());
        if (shippingDate.before(new Date(System.currentTimeMillis()))) {
            throw new DataNotFoundException("Date must be at least today");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return modelMapper.map(orderRepository.save(order), OrderResponse.class);
    }

    @Override
    public OrderResponse getOrderById(Long id) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        // Check if order exists, if not, throw an exception
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + id));
        // Check if user exists, if not, throw an exception
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id: " + orderDTO.getUserId()));
        // Create a mapping between OrderDTO and Order
        // Do not skip user
        // skip id, orderDate, createdAt, updatedAt
        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(modelMapper -> {
            modelMapper.skip(Order::setId);
            modelMapper.skip(Order::setOrderDate);
            modelMapper.skip(Order::setCreatedAt);
            modelMapper.skip(Order::setUpdatedAt);
        });
        // Update existing order with new information
        modelMapper.map(orderDTO, existingOrder);
        existingOrder.setUser(existingUser);
        return modelMapper.map(orderRepository.save(existingOrder), OrderResponse.class);
    }

    @Override
    public void deleteOrder(Long id) {
        // no hard delete, just set active to false
        Order order = orderRepository.findById(id)
                .orElse(null);
        if (order != null) {
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<OrderResponse> findByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(order -> modelMapper.map(order, OrderResponse.class))
                .toList();
    }
}

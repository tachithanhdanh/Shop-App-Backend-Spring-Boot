package com.example.shopapp.controllers;


import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.responses.OrderResponse;
import com.example.shopapp.services.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderDTO orderDTO,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            OrderResponse orderResponse = orderService.createOrder(orderDTO);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{user_id}") // Get orders by user ID
    // GET http://localhost:8088/api/v1/orders/1
    public ResponseEntity<?> getOrdersByUserId(@Valid @PathVariable("user_id") Long userId) {
        try {
            // Get orders by user ID
            return ResponseEntity.ok("Orders by user ID: " + userId);
        } catch (Exception e) {
            // Exception occurs when getting order because of invalid user ID
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{order_id}") // Update order by order ID
    // PUT http://localhost:8088/api/v1/orders/1
    // Admin can update order information
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable("order_id") Long orderId,
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult bindingResult
            ) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            return ResponseEntity.ok("Order updated successfully: " + orderDTO.toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{order_id}") // Delete order by order ID
    // DELETE http://localhost:8088/api/v1/orders/1
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable("order_id") Long orderId) {
        // soft delete order by order ID (set active = false)
        return ResponseEntity.ok("Order deleted successfully: " + orderId);
    }
}

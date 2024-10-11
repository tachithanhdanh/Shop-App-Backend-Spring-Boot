package com.example.shopapp.controllers;

import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.models.OrderDetail;
import com.example.shopapp.responses.OrderDetailResponse;
import com.example.shopapp.services.OrderDetailService;
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
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            OrderDetail orderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable @Valid Long id) {
        try {
            OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
            OrderDetailResponse orderDetailResponse = OrderDetailResponse.fromOrderDetail(
                    orderDetail);
            return ResponseEntity.ok(orderDetailResponse);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<?> getOrderDetailsByOrderId(
            @PathVariable("order_id") @Valid Long orderId) {
        try {
            List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
            List<OrderDetailResponse> orderDetailResponses = orderDetails
                    .stream()
                    .map(OrderDetailResponse::fromOrderDetail)
                    .toList();
            return ResponseEntity.ok(orderDetailResponses);
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@Valid @PathVariable Long id,
            @Valid @RequestBody OrderDetailDTO newOrderDetailData,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, newOrderDetailData);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderDetail(@PathVariable @Valid Long id) {
        orderDetailService.deleteById(id);
        return ResponseEntity.ok("Order detail with id = " + id + " has been deleted.");
    }
}

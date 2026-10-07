package za.ac.cput.communitystore.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.communitystore.dto.CheckoutRequest;
import za.ac.cput.communitystore.dto.OrderResponse;
import za.ac.cput.communitystore.entity.Order;
import za.ac.cput.communitystore.enums.OrderStatus;
import za.ac.cput.communitystore.service.OrderService;
import za.ac.cput.communitystore.util.ResponseMapper;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout/{userId}")
    @PreAuthorize("@securityService.isCurrentUser(#userId)")
    public ResponseEntity<OrderResponse> checkout(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest request) {

        return ResponseEntity.ok(
                ResponseMapper.toOrderResponse(
                        orderService.checkout(userId, request)
                )
        );
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("@securityService.canViewOrder(#orderId)")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                ResponseMapper.toOrderResponse(
                        orderService.getOrderById(orderId)
                )
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("@securityService.isCurrentUser(#userId)")
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                orderService.getOrdersByUser(userId)
                        .stream()
                        .map(ResponseMapper::toOrderResponse)
                        .toList()
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER_SUPPORT', 'STORE_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
                        .stream()
                        .map(ResponseMapper::toOrderResponse)
                        .toList()
        );
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('CUSTOMER_SUPPORT', 'STORE_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                ResponseMapper.toOrderResponse(
                        orderService.updateOrderStatus(orderId, status)
                )
        );
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("@securityService.canAccessOrder(#orderId)")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId) {

        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
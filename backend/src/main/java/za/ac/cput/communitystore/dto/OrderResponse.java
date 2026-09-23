package za.ac.cput.communitystore.dto;

import za.ac.cput.communitystore.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal total;
    private LocalDateTime orderDate;
    private String deliveryAddress;
    private List<OrderItemResponse> items;

    public OrderResponse() {
    }

    public OrderResponse(
            Long id,
            Long userId,
            OrderStatus status,
            BigDecimal total,
            LocalDateTime orderDate,
            String deliveryAddress,
            List<OrderItemResponse> items) {

        this.id = id;
        this.userId = userId;
        this.status = status;
        this.total = total;
        this.orderDate = orderDate;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
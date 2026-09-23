package za.ac.cput.communitystore.service;

import za.ac.cput.communitystore.dto.CheckoutRequest;
import za.ac.cput.communitystore.entity.Order;
import za.ac.cput.communitystore.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    Order checkout(Long userId, CheckoutRequest request);

    Order getOrderById(Long orderId);

    List<Order> getOrdersByUser(Long userId);

    List<Order> getAllOrders();

    Order updateOrderStatus(
            Long orderId,
            OrderStatus status
    );

    void cancelOrder(Long orderId);
}
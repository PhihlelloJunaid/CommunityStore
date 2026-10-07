package za.ac.cput.communitystore.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.cput.communitystore.dto.CheckoutRequest;
import za.ac.cput.communitystore.entity.Cart;
import za.ac.cput.communitystore.entity.CartItem;
import za.ac.cput.communitystore.entity.Order;
import za.ac.cput.communitystore.entity.OrderItem;
import za.ac.cput.communitystore.entity.Product;
import za.ac.cput.communitystore.entity.User;
import za.ac.cput.communitystore.enums.NotificationType;
import za.ac.cput.communitystore.enums.OrderStatus;
import za.ac.cput.communitystore.repository.CartRepository;
import za.ac.cput.communitystore.repository.OrderRepository;
import za.ac.cput.communitystore.repository.UserRepository;
import za.ac.cput.communitystore.service.NotificationService;
import za.ac.cput.communitystore.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository,
            NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Order checkout(Long userId, CheckoutRequest request) {
        if (request == null || request.getDeliveryAddress() == null ||
                request.getDeliveryAddress().isBlank()) {
            throw new IllegalArgumentException("Delivery address is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot checkout an empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryAddress(request.getDeliveryAddress().trim());

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();

            if (requestedQuantity <= 0) {
                throw new IllegalArgumentException("Cart quantity must be greater than zero");
            }

            if (product.getQuantity() < requestedQuantity) {
                throw new IllegalArgumentException(
                        "Not enough stock for product: " + product.getName());
            }

            BigDecimal price = product.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(requestedQuantity));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(requestedQuantity);
            orderItem.setPrice(price);
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);

            total = total.add(subtotal);
            product.setQuantity(product.getQuantity() - requestedQuantity);
        }

        order.setItems(orderItems);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);

        notificationService.createNotification(
                userId,
                "Order placed",
                "Order #" + savedOrder.getId() + " has been placed and is waiting for payment.",
                NotificationType.ORDER
        );

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Order status is required");
        }

        Order order = getOrderById(orderId);
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);

        notificationService.createNotification(
                order.getUser().getId(),
                "Order status updated",
                "Order #" + order.getId() + " is now " +
                        status.name().replace("_", " ").toLowerCase() + ".",
                NotificationType.ORDER
        );

        return savedOrder;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalArgumentException("A delivered order cannot be cancelled");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Order is already cancelled");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalArgumentException("This order can no longer be cancelled");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        notificationService.createNotification(
                order.getUser().getId(),
                "Order cancelled",
                "Order #" + order.getId() + " has been cancelled and the items were returned to stock.",
                NotificationType.ORDER
        );
    }
}

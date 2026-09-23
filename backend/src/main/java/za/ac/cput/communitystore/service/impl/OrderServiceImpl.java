package za.ac.cput.communitystore.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.cput.communitystore.dto.CheckoutRequest;
import za.ac.cput.communitystore.entity.Cart;
import za.ac.cput.communitystore.entity.CartItem;
import za.ac.cput.communitystore.entity.Order;
import za.ac.cput.communitystore.entity.OrderItem;
import za.ac.cput.communitystore.entity.Product;
import za.ac.cput.communitystore.entity.User;
import za.ac.cput.communitystore.enums.OrderStatus;
import za.ac.cput.communitystore.repository.CartRepository;
import za.ac.cput.communitystore.repository.OrderRepository;
import za.ac.cput.communitystore.repository.UserRepository;
import za.ac.cput.communitystore.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Order checkout(
            Long userId,
            CheckoutRequest request) {

        if (request == null ||
                request.getDeliveryAddress() == null ||
                request.getDeliveryAddress().isBlank()) {

            throw new RuntimeException(
                    "Delivery address is required"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));

        if (cart.getItems() == null ||
                cart.getItems().isEmpty()) {

            throw new RuntimeException(
                    "Cannot checkout an empty cart"
            );
        }

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryAddress(
                request.getDeliveryAddress()
        );

        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            int requestedQuantity =
                    cartItem.getQuantity();

            if (product.getQuantity() < requestedQuantity) {

                throw new RuntimeException(
                        "Not enough stock for product: "
                                + product.getName()
                );
            }

            BigDecimal price =
                    product.getPrice();

            BigDecimal subtotal =
                    price.multiply(
                            BigDecimal.valueOf(
                                    requestedQuantity
                            )
                    );

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(requestedQuantity);
            orderItem.setPrice(price);
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);

            total = total.add(subtotal);

            // Reduce stock
            product.setQuantity(
                    product.getQuantity()
                            - requestedQuantity
            );
        }

        order.setItems(orderItems);
        order.setTotal(total);

        Order savedOrder =
                orderRepository.save(order);

        // Clear cart after successful checkout
        cart.getItems().clear();
        cart.setTotal(BigDecimal.ZERO);

        cartRepository.save(cart);

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Order not found"
                        ));
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {

        return orderRepository
                .findByUserIdOrderByOrderDateDesc(userId);
    }

    @Override
    public List<Order> getAllOrders() {

        return orderRepository
                .findAll();
    }

    @Override
    public Order updateOrderStatus(
            Long orderId,
            OrderStatus status) {

        if (status == null) {
            throw new RuntimeException(
                    "Order status is required"
            );
        }

        Order order =
                getOrderById(orderId);

        order.setStatus(status);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {

        Order order =
                getOrderById(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED) {

            throw new RuntimeException(
                    "A delivered order cannot be cancelled"
            );
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new RuntimeException(
                    "Order is already cancelled"
            );
        }

        // Return stock
        for (OrderItem item : order.getItems()) {

            Product product =
                    item.getProduct();

            product.setQuantity(
                    product.getQuantity()
                            + item.getQuantity()
            );
        }

        order.setStatus(
                OrderStatus.CANCELLED
        );

        orderRepository.save(order);
    }
}
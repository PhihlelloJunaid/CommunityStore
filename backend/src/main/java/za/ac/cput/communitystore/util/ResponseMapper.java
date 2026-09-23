package za.ac.cput.communitystore.util;

import za.ac.cput.communitystore.dto.*;
import za.ac.cput.communitystore.entity.*;

import java.util.List;

public class ResponseMapper {

    private ResponseMapper() {
    }

    public static UserResponse toUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getStudentNumber(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public static CategoryResponse toCategoryResponse(
            Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    public static ProductResponse toProductResponse(
            Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),

                product.getCategory().getId(),
                product.getCategory().getName(),

                product.getStore().getId(),
                product.getStore().getStoreName()
        );
    }

    public static StoreResponse toStoreResponse(
            Store store) {

        User owner = store.getOwner();

        String ownerName =
                owner.getFirstName()
                        + " "
                        + owner.getLastName();

        return new StoreResponse(
                store.getId(),
                store.getStoreName(),
                store.getDescription(),
                store.getLocation(),
                owner.getId(),
                ownerName
        );
    }

    public static CartItemResponse toCartItemResponse(
            CartItem item) {

        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }

    public static CartResponse toCartResponse(
            Cart cart) {

        List<CartItemResponse> items =
                cart.getItems()
                        .stream()
                        .map(ResponseMapper::toCartItemResponse)
                        .toList();

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                items,
                cart.getTotal()
        );
    }

    public static OrderItemResponse toOrderItemResponse(
            OrderItem item) {

        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
        );
    }

    public static OrderResponse toOrderResponse(
            Order order) {

        List<OrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(ResponseMapper::toOrderItemResponse)
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotal(),
                order.getOrderDate(),
                order.getDeliveryAddress(),
                items
        );
    }
}
package za.ac.cput.communitystore.service;

import za.ac.cput.communitystore.entity.Cart;

public interface CartService {

    Cart createCart(Long userId);

    Cart getCartByUserId(Long userId);

    Cart addProduct(Long userId, Long productId, Integer quantity);

    Cart updateItemQuantity(
            Long userId,
            Long productId,
            Integer quantity
    );

    Cart removeProduct(Long userId, Long productId);

    void clearCart(Long userId);
}
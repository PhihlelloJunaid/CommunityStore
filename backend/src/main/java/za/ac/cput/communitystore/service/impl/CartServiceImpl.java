package za.ac.cput.communitystore.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.cput.communitystore.entity.Cart;
import za.ac.cput.communitystore.entity.CartItem;
import za.ac.cput.communitystore.entity.Product;
import za.ac.cput.communitystore.entity.User;
import za.ac.cput.communitystore.repository.CartItemRepository;
import za.ac.cput.communitystore.repository.CartRepository;
import za.ac.cput.communitystore.repository.ProductRepository;
import za.ac.cput.communitystore.repository.UserRepository;
import za.ac.cput.communitystore.service.CartService;

import java.math.BigDecimal;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Cart createCart(Long userId) {

        if (cartRepository.existsByUserId(userId)) {
            return cartRepository.findByUserId(userId)
                    .orElseThrow(() ->
                            new RuntimeException("Cart not found"));
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Cart cart = Cart.builder()
                .user(user)
                .total(BigDecimal.ZERO)
                .build();

        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartByUserId(Long userId) {

        return cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException("Cart not found"));
    }

    @Override
    @Transactional
    public Cart addProduct(
            Long userId,
            Long productId,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        Cart cart;

        if (cartRepository.existsByUserId(userId)) {
            cart = getCartByUserId(userId);
        } else {
            cart = createCart(userId);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException(
                    "Not enough product stock available"
            );
        }

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId
                        )
                        .orElse(null);

        if (cartItem == null) {

            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(quantity)
                            );

            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .subtotal(subtotal)
                    .build();

            cart.getItems().add(cartItem);

        } else {

            int newQuantity =
                    cartItem.getQuantity() + quantity;

            if (newQuantity > product.getQuantity()) {
                throw new RuntimeException(
                        "Not enough product stock available"
                );
            }

            cartItem.setQuantity(newQuantity);

            cartItem.setSubtotal(
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(newQuantity)
                            )
            );
        }

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateItemQuantity(
            Long userId,
            Long productId,
            Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        Cart cart = getCartByUserId(userId);

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product is not in cart"
                                ));

        Product product = cartItem.getProduct();

        if (quantity > product.getQuantity()) {
            throw new RuntimeException(
                    "Not enough product stock available"
            );
        }

        cartItem.setQuantity(quantity);

        cartItem.setSubtotal(
                product.getPrice()
                        .multiply(
                                BigDecimal.valueOf(quantity)
                        )
        );

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeProduct(
            Long userId,
            Long productId) {

        Cart cart = getCartByUserId(userId);

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product is not in cart"
                                ));

        cart.getItems().remove(cartItem);

        cartItemRepository.delete(cartItem);

        recalculateTotal(cart);

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {

        Cart cart = getCartByUserId(userId);

        cart.getItems().clear();

        cart.setTotal(BigDecimal.ZERO);

        cartRepository.save(cart);
    }

    private void recalculateTotal(Cart cart) {

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {

            if (item.getSubtotal() != null) {
                total = total.add(item.getSubtotal());
            }
        }

        cart.setTotal(total);
    }
}
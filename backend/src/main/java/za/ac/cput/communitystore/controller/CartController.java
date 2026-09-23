package za.ac.cput.communitystore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import za.ac.cput.communitystore.dto.CartResponse;
import za.ac.cput.communitystore.entity.Cart;
import za.ac.cput.communitystore.service.CartService;
import za.ac.cput.communitystore.util.ResponseMapper;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CartResponse> createCart(
            @PathVariable Long userId) {

        Cart cart = cartService.createCart(userId);

        return new ResponseEntity<>(
                ResponseMapper.toCartResponse(cart),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponse> getCart(
            @PathVariable Long userId) {

        Cart cart = cartService.getCartByUserId(userId);

        return ResponseEntity.ok(
                ResponseMapper.toCartResponse(cart)
        );
    }

    @PostMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<CartResponse> addProduct(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        Cart cart = cartService.addProduct(
                userId,
                productId,
                quantity
        );

        return ResponseEntity.ok(
                ResponseMapper.toCartResponse(cart)
        );
    }

    @PutMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        Cart cart = cartService.updateItemQuantity(
                userId,
                productId,
                quantity
        );

        return ResponseEntity.ok(
                ResponseMapper.toCartResponse(cart)
        );
    }

    @DeleteMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<CartResponse> removeProduct(
            @PathVariable Long userId,
            @PathVariable Long productId) {

        Cart cart = cartService.removeProduct(
                userId,
                productId
        );

        return ResponseEntity.ok(
                ResponseMapper.toCartResponse(cart)
        );
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearCart(
            @PathVariable Long userId) {

        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
}
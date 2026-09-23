package za.ac.cput.communitystore.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import za.ac.cput.communitystore.dto.*;
import za.ac.cput.communitystore.entity.*;
import za.ac.cput.communitystore.service.AdminService;
import za.ac.cput.communitystore.util.ResponseMapper;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsers() {

        List<UserResponse> users =
                adminService.getAllUsers()
                        .stream()
                        .map(ResponseMapper::toUserResponse)
                        .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts() {

        List<ProductResponse> products =
                adminService.getAllProducts()
                        .stream()
                        .map(ResponseMapper::toProductResponse)
                        .toList();

        return ResponseEntity.ok(products);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories() {

        List<CategoryResponse> categories =
                adminService.getAllCategories()
                        .stream()
                        .map(ResponseMapper::toCategoryResponse)
                        .toList();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/stores")
    public ResponseEntity<List<StoreResponse>> getStores() {

        List<StoreResponse> stores =
                adminService.getAllStores()
                        .stream()
                        .map(ResponseMapper::toStoreResponse)
                        .toList();

        return ResponseEntity.ok(stores);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {

        List<OrderResponse> orders =
                adminService.getAllOrders()
                        .stream()
                        .map(ResponseMapper::toOrderResponse)
                        .toList();

        return ResponseEntity.ok(orders);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        adminService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        adminService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id) {

        adminService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/stores/{id}")
    public ResponseEntity<Void> deleteStore(
            @PathVariable Long id) {

        adminService.deleteStore(id);

        return ResponseEntity.noContent().build();
    }
}
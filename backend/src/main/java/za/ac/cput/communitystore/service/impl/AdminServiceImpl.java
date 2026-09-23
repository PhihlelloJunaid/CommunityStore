package za.ac.cput.communitystore.service.impl;

import org.springframework.stereotype.Service;

import za.ac.cput.communitystore.entity.Category;
import za.ac.cput.communitystore.entity.Order;
import za.ac.cput.communitystore.entity.Product;
import za.ac.cput.communitystore.entity.Store;
import za.ac.cput.communitystore.entity.User;

import za.ac.cput.communitystore.repository.CategoryRepository;
import za.ac.cput.communitystore.repository.OrderRepository;
import za.ac.cput.communitystore.repository.ProductRepository;
import za.ac.cput.communitystore.repository.StoreRepository;
import za.ac.cput.communitystore.repository.UserRepository;

import za.ac.cput.communitystore.service.AdminService;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    public AdminServiceImpl(
            UserRepository userRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            StoreRepository storeRepository,
            OrderRepository orderRepository) {

        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.storeRepository = storeRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public void deleteUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(userId);
    }

    @Override
    public void deleteProduct(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }

        productRepository.deleteById(productId);
    }

    @Override
    public void deleteCategory(Long categoryId) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new RuntimeException("Category not found");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    public void deleteStore(Long storeId) {

        if (!storeRepository.existsById(storeId)) {
            throw new RuntimeException("Store not found");
        }

        storeRepository.deleteById(storeId);
    }
}
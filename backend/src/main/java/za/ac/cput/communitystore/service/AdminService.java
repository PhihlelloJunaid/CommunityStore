package za.ac.cput.communitystore.service;

import za.ac.cput.communitystore.entity.Category;
import za.ac.cput.communitystore.entity.Order;
import za.ac.cput.communitystore.entity.Product;
import za.ac.cput.communitystore.entity.Store;
import za.ac.cput.communitystore.entity.User;

import java.util.List;

public interface AdminService {

    List<User> getAllUsers();

    List<Product> getAllProducts();

    List<Category> getAllCategories();

    List<Store> getAllStores();

    List<Order> getAllOrders();

    void deleteUser(Long userId);

    void deleteProduct(Long productId);

    void deleteCategory(Long categoryId);

    void deleteStore(Long storeId);
}
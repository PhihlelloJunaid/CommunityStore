package za.ac.cput.communitystore.service;

import za.ac.cput.communitystore.entity.Product;

import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    Product getProductById(Long id);

    List<Product> getAllProducts();

    List<Product> searchProducts(String name);

    List<Product> getProductsByCategory(Long categoryId);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);
}
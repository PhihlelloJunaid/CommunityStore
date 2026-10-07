package za.ac.cput.communitystore.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.communitystore.entity.Product;
import za.ac.cput.communitystore.enums.ProductCondition;
import za.ac.cput.communitystore.repository.ProductRepository;
import za.ac.cput.communitystore.service.ProductService;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product) {
        validate(product);
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public Product updateProduct(Long id, Product product) {

        validate(product);
        Product existingProduct = getProductById(id);

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setCondition(product.getCondition());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setStore(product.getStore());

        return productRepository.save(existingProduct);
    }

    private void validate(Product product) {
        if (product == null) throw new IllegalArgumentException("Product is required");
        if (product.getPrice() == null || product.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (product.getCondition() == null) {
            product.setCondition(ProductCondition.GOOD);
        }

        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = getProductById(id);

        productRepository.delete(product);
    }
}
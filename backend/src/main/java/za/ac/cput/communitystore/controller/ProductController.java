package za.ac.cput.communitystore.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.communitystore.dto.ProductRequest;
import za.ac.cput.communitystore.dto.ProductResponse;
import za.ac.cput.communitystore.entity.Product;
import za.ac.cput.communitystore.enums.ProductCondition;
import za.ac.cput.communitystore.repository.CategoryRepository;
import za.ac.cput.communitystore.repository.StoreRepository;
import za.ac.cput.communitystore.service.ProductService;
import za.ac.cput.communitystore.util.ResponseMapper;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    public ProductController(
            ProductService productService,
            CategoryRepository categoryRepository,
            StoreRepository storeRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.storeRepository = storeRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STORE_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        Product product = toEntity(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ResponseMapper.toProductResponse(
                                productService.createProduct(product)
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(
                productService.getAllProducts()
                        .stream()
                        .map(ResponseMapper::toProductResponse)
                        .toList()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam String name) {

        return ResponseEntity.ok(
                productService.searchProducts(name)
                        .stream()
                        .map(ResponseMapper::toProductResponse)
                        .toList()
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryId)
                        .stream()
                        .map(ResponseMapper::toProductResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseMapper.toProductResponse(
                        productService.getProductById(id)
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STORE_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                ResponseMapper.toProductResponse(
                        productService.updateProduct(
                                id,
                                toEntity(request)
                        )
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STORE_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    private Product toEntity(ProductRequest request) {
        Product product = new Product();

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setCondition(request.condition() == null ? ProductCondition.GOOD : request.condition());

        product.setCategory(
                categoryRepository.findById(request.categoryId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Category not found"
                                ))
        );

        if (request.storeId() != null) {
            product.setStore(
                    storeRepository.findById(request.storeId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Store not found"
                                    ))
            );
        }

        return product;
    }
}
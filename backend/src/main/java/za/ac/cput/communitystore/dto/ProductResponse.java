package za.ac.cput.communitystore.dto;

import java.math.BigDecimal;

import za.ac.cput.communitystore.enums.ProductCondition;

public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private ProductCondition condition;
    private Long categoryId;
    private String categoryName;
    private Long storeId;
    private String storeName;

    public ProductResponse() {
    }

    public ProductResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer quantity,
            ProductCondition condition,
            Long categoryId,
            String categoryName,
            Long storeId,
            String storeName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.condition = condition;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.storeId = storeId;
        this.storeName = storeName;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public ProductCondition getCondition() { return condition; }
    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public Long getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
}

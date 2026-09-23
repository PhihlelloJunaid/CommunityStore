package za.ac.cput.communitystore.dto;

public class StoreResponse {

    private Long id;
    private String storeName;
    private String description;
    private String location;

    private Long ownerId;
    private String ownerName;

    public StoreResponse() {
    }

    public StoreResponse(
            Long id,
            String storeName,
            String description,
            String location,
            Long ownerId,
            String ownerName) {

        this.id = id;
        this.storeName = storeName;
        this.description = description;
        this.location = location;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    public Long getId() {
        return id;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
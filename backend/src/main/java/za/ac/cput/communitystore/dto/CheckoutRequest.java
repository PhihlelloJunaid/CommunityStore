package za.ac.cput.communitystore.dto;

public class CheckoutRequest {

    private String deliveryAddress;

    public CheckoutRequest() {
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
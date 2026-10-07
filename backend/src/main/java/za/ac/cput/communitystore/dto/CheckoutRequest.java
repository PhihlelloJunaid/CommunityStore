package za.ac.cput.communitystore.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckoutRequest {
    @NotBlank private String deliveryAddress;
    public CheckoutRequest() {}
    public String getDeliveryAddress(){return deliveryAddress;}
    public void setDeliveryAddress(String deliveryAddress){this.deliveryAddress=deliveryAddress;}
}

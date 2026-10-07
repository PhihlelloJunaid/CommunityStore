package za.ac.cput.communitystore.dto;

import jakarta.validation.constraints.NotBlank;

public record SupportRequestCreateRequest(
        @NotBlank String subject,
        @NotBlank String description,
        Long orderId
) {
}
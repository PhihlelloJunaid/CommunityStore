package za.ac.cput.communitystore.dto;

import jakarta.validation.constraints.NotNull;
import za.ac.cput.communitystore.enums.SupportRequestStatus;

public record SupportRequestUpdateRequest(
        @NotNull SupportRequestStatus status,
        String response
) {
}
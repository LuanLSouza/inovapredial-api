package com.inovapredial.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WorkOrderInventoryRequestDTO(
    @NotNull(message = "Inventory ID is required")
    String inventoryId,
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    Integer quantity
) {
}

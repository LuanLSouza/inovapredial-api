package com.inovapredial.dto.responses;

import com.inovapredial.model.enums.ItemType;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryResponseDTO(
        UUID id,
        ItemType itemType,
        String name,
        BigDecimal cost,
        Integer quantity,
        Integer minimumStock,
        UUID buildingId,
        UUID employeeId
) {
}


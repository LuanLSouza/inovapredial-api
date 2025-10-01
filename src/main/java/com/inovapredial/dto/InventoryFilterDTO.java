package com.inovapredial.dto;

import com.inovapredial.model.enums.ItemType;

import java.util.UUID;

public record InventoryFilterDTO(
        ItemType itemType,
        String name,
        UUID employeeId
) {
}


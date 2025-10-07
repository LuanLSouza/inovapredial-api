package com.inovapredial.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WorkOrderInventoryResponseDTO(
    String inventoryId,
    String inventoryName,
    Integer quantity,
    BigDecimal unitCost,
    BigDecimal totalCost,
    LocalDateTime outputDate
) {
}

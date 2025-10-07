package com.inovapredial.dto.responses;

import com.inovapredial.model.enums.ActivityStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponseDTO(
        UUID id,
        String title,
        String description,
        String reason,
        ActivityStatus activityStatus,
        Integer estimatedTime,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer timeSpent,
        BigDecimal cost,
        UUID workOrderId,
        UUID employeeId,
        UUID buildingId
) {
}



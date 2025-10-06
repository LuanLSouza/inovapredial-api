package com.inovapredial.dto.responses;

import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.model.enums.MaintenanceType;
import com.inovapredial.model.enums.Priority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkOrderResponseDTO(
        UUID id,
        String description,
        LocalDateTime openingDate,
        LocalDateTime closingDate,
        ActivityStatus activityStatus,
        Priority priority,
        MaintenanceType maintenanceType,
        BigDecimal totalCost,
        UUID equipmentId,
        UUID employeeId,
        UUID buildingId
) {
}

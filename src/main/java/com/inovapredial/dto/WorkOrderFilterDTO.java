package com.inovapredial.dto;

import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.model.enums.MaintenanceType;
import com.inovapredial.model.enums.Priority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkOrderFilterDTO(
        String description,
        LocalDateTime openingDateStart,
        LocalDateTime openingDateEnd,
        LocalDateTime closingDateStart,
        LocalDateTime closingDateEnd,
        ActivityStatus activityStatus,
        Priority priority,
        MaintenanceType maintenanceType,
        UUID equipmentId,
        UUID employeeId,
        BigDecimal totalCostMin,
        BigDecimal totalCostMax
) {
}

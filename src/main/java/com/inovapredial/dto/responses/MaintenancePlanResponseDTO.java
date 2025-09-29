package com.inovapredial.dto.responses;

import com.inovapredial.model.enums.MaintenanceType;

import java.util.UUID;

public record MaintenancePlanResponseDTO(
        UUID id,
        Integer frequencyDays,
        Boolean requiresShutdown,
        String description,
        MaintenanceType maintenanceType,
        UUID buildingId
) {
}

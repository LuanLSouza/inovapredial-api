package com.inovapredial.dto;

import com.inovapredial.model.enums.MaintenanceType;

public record MaintenancePlanFilterDTO(
        Integer frequencyDays,
        Boolean requiresShutdown,
        String description,
        MaintenanceType maintenanceType
) {
}

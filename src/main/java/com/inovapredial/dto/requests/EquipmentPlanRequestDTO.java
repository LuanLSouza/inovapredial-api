package com.inovapredial.dto.requests;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EquipmentPlanRequestDTO(
        @NotNull(message = "Equipment ID is required")
        String equipmentId,
        
        @NotNull(message = "Plan ID is required")
        String planId,
        
        @NotNull(message = "Start date is required")
        LocalDate startDate
) {
}

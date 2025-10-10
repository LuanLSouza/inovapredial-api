package com.inovapredial.dto.responses;

import java.time.LocalDate;
import java.util.UUID;

public record EquipmentPlanResponseDTO(
        UUID equipmentId,
        String equipmentIdentification,
        UUID planId,
        String planDescription,
        LocalDate startDate,
        LocalDate nextDueDate,
        boolean realized,
        UUID buildingId
) {
}

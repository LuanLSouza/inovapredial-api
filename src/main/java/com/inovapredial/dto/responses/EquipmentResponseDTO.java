package com.inovapredial.dto.responses;

import com.inovapredial.model.enums.Criticality;
import com.inovapredial.model.enums.EquipmentStatus;
import com.inovapredial.model.enums.EquipmentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EquipmentResponseDTO(
        UUID id,
        String identification,
        String description,
        String serialNumber,
        EquipmentType classification,
        String location,
        Criticality criticality,
        LocalDate purchaseDate,
        LocalDate warrantyEndDate,
        BigDecimal price,
        EquipmentStatus equipmentStatus,
        String imageUrl,
        String group,
        String model,
        String costCenter,
        CalendarResponseDTO calendar,
        UUID buildingId
) {
}

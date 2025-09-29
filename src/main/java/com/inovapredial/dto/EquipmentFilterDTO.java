package com.inovapredial.dto;

import com.inovapredial.model.enums.Criticality;
import com.inovapredial.model.enums.EquipmentStatus;
import com.inovapredial.model.enums.EquipmentType;

public record EquipmentFilterDTO(
        String identification,
        String description,
        String serialNumber,
        EquipmentType classification,
        String location,
        Criticality criticality,
        EquipmentStatus equipmentStatus,
        String group,
        String model,
        String costCenter
) {
}

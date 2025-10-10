package com.inovapredial.mapper;

import com.inovapredial.dto.responses.EquipmentPlanResponseDTO;
import com.inovapredial.model.EquipmentPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EquipmentPlanMapper {

    @Mapping(source = "equipment.id", target = "equipmentId")
    @Mapping(source = "equipment.identification", target = "equipmentIdentification")
    @Mapping(source = "maintenancePlan.id", target = "planId")
    @Mapping(source = "maintenancePlan.description", target = "planDescription")
    @Mapping(source = "building.id", target = "buildingId")
    EquipmentPlanResponseDTO toResponseDTO(EquipmentPlan equipmentPlan);
}

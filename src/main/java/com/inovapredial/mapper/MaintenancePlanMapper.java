package com.inovapredial.mapper;

import com.inovapredial.dto.requests.MaintenancePlanRequestDTO;
import com.inovapredial.dto.responses.MaintenancePlanResponseDTO;
import com.inovapredial.model.MaintenancePlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MaintenancePlanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownUser", ignore = true)
    @Mapping(target = "building", ignore = true)
    MaintenancePlan toEntity(MaintenancePlanRequestDTO dto);

    @Mapping(source = "building.id", target = "buildingId")
    MaintenancePlanResponseDTO toResponseDTO(MaintenancePlan maintenancePlan);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownUser", ignore = true)
    @Mapping(target = "building", ignore = true)
    void updateMaintenancePlanFromRequestDTO(MaintenancePlanRequestDTO dto, @MappingTarget MaintenancePlan maintenancePlan);
}

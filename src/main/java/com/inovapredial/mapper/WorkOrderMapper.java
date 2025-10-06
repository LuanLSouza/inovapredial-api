package com.inovapredial.mapper;

import com.inovapredial.dto.requests.WorkOrderRequestDTO;
import com.inovapredial.dto.responses.WorkOrderResponseDTO;
import com.inovapredial.model.WorkOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "building", ignore = true)
    WorkOrder toEntity(WorkOrderRequestDTO dto);

    @Mapping(source = "equipment.id", target = "equipmentId")
    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "building.id", target = "buildingId")
    WorkOrderResponseDTO toResponseDTO(WorkOrder workOrder);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "building", ignore = true)
    void updateWorkOrderFromRequestDTO(WorkOrderRequestDTO dto, @MappingTarget WorkOrder workOrder);
}

package com.inovapredial.mapper;

import com.inovapredial.dto.requests.InventoryRequestDTO;
import com.inovapredial.dto.responses.InventoryResponseDTO;
import com.inovapredial.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "building", ignore = true)
    Inventory toEntity(InventoryRequestDTO dto);

    @Mapping(source = "building.id", target = "buildingId")
    @Mapping(source = "employee.id", target = "employeeId")
    InventoryResponseDTO toResponseDTO(Inventory inventory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "building", ignore = true)
    void updateInventoryFromRequestDTO(InventoryRequestDTO dto, @MappingTarget Inventory inventory);
}


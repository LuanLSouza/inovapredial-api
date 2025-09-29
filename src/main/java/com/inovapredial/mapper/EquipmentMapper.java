package com.inovapredial.mapper;

import com.inovapredial.dto.CalendarRequestDTO;
import com.inovapredial.dto.CalendarResponseDTO;
import com.inovapredial.dto.EquipmentRequestDTO;
import com.inovapredial.dto.EquipmentResponseDTO;
import com.inovapredial.model.Calendar;
import com.inovapredial.model.Equipment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "calendar", ignore = true)
    @Mapping(target = "ownUser", ignore = true)
    @Mapping(target = "building", ignore = true)
    Equipment toEntity(EquipmentRequestDTO dto);

    @Mapping(source = "calendar", target = "calendar")
    @Mapping(source = "building.id", target = "buildingId")
    EquipmentResponseDTO toResponseDTO(Equipment equipment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "calendar", ignore = true)
    @Mapping(target = "ownUser", ignore = true)
    @Mapping(target = "building", ignore = true)
    void updateEquipmentFromRequestDTO(EquipmentRequestDTO dto, @MappingTarget Equipment equipment);

    Calendar toEntity(CalendarRequestDTO dto);

    CalendarResponseDTO toResponseDTO(Calendar calendar);
}

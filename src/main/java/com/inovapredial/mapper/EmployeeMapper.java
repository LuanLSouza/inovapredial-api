package com.inovapredial.mapper;

import com.inovapredial.dto.requests.CalendarRequestDTO;
import com.inovapredial.dto.requests.EmployeeRequestDTO;
import com.inovapredial.dto.responses.CalendarResponseDTO;
import com.inovapredial.dto.responses.EmployeeResponseDTO;
import com.inovapredial.model.Calendar;
import com.inovapredial.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "calendar", ignore = true)
    @Mapping(target = "building", ignore = true)
    Employee toEntity(EmployeeRequestDTO dto);

    @Mapping(source = "calendar", target = "calendar")
    @Mapping(source = "building.id", target = "buildingId")
    EmployeeResponseDTO toResponseDTO(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "calendar", ignore = true)
    @Mapping(target = "building", ignore = true)
    void updateEmployeeFromRequestDTO(EmployeeRequestDTO dto, @MappingTarget Employee employee);

    Calendar toEntity(CalendarRequestDTO dto);

    CalendarResponseDTO toResponseDTO(Calendar calendar);
}

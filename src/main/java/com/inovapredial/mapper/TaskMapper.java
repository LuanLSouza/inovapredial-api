package com.inovapredial.mapper;

import com.inovapredial.dto.requests.TaskRequestDTO;
import com.inovapredial.dto.responses.TaskResponseDTO;
import com.inovapredial.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workOrder", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "building", ignore = true)
    Task toEntity(TaskRequestDTO dto);

    @Mapping(source = "workOrder.id", target = "workOrderId")
    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "building.id", target = "buildingId")
    TaskResponseDTO toResponseDTO(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workOrder", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "building", ignore = true)
    void updateTaskFromRequestDTO(TaskRequestDTO dto, @MappingTarget Task task);
}



package com.inovapredial.service;

import com.inovapredial.dto.TaskFilterDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.requests.TaskRequestDTO;
import com.inovapredial.dto.responses.TaskResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.TaskMapper;
import com.inovapredial.model.Building;
import com.inovapredial.model.Employee;
import com.inovapredial.model.OwnUser;
import com.inovapredial.model.Task;
import com.inovapredial.model.WorkOrder;
import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.repository.TaskRepository;
import com.inovapredial.specification.TaskSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper mapper;
    private final TaskRepository taskRepository;
    private final BuildingService buildingService;
    private final WorkOrderService workOrderService;
    private final EmployeeService employeeService;
    private final SecurityContextService securityContextService;

    @Transactional
    public Task create(TaskRequestDTO dto, String buildingId) {
        var saved = createInternal(dto, buildingId);
        return taskRepository.save(saved);
    }

    @Transactional
    public List<Task> createBatch(List<TaskRequestDTO> dtos, String buildingId) {
        List<Task> toSave = new ArrayList<>();
        for (TaskRequestDTO dto : dtos) {
            toSave.add(createInternal(dto, buildingId));
        }
        return taskRepository.saveAll(toSave);
    }

    private Task createInternal(TaskRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        WorkOrder workOrder = workOrderService.findByIdAndBuilding(dto.workOrderId().toString(), buildingId);
        if (!workOrder.getBuilding().getId().equals(building.getId())) {
            throw new NotFoundException("WorkOrder not found in this building");
        }

        Task toSave = mapper.toEntity(dto);
        toSave.setWorkOrder(workOrder);
        toSave.setBuilding(building);

        if (dto.employeeId() != null) {
            Employee employee = employeeService.findByIdAndBuilding(dto.employeeId().toString(), buildingId);
            if (!employee.getBuilding().getId().equals(building.getId())) {
                throw new NotFoundException("Employee not found in this building");
            }
            toSave.setEmployee(employee);
        }

        if (toSave.getActivityStatus() == null) {
            toSave.setActivityStatus(ActivityStatus.OPEN);
        }

        return toSave;
    }

    @Transactional
    public Task update(String id, TaskRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        Task taskToUpdate = findByIdAndBuilding(id, buildingId);

        if (dto.workOrderId() != null) {
            WorkOrder workOrder = workOrderService.findByIdAndBuilding(dto.workOrderId().toString(), buildingId);
            if (!workOrder.getBuilding().getId().equals(building.getId())) {
                throw new NotFoundException("WorkOrder not found in this building");
            }
            taskToUpdate.setWorkOrder(workOrder);
        }

        if (dto.employeeId() != null) {
            Employee employee = employeeService.findByIdAndBuilding(dto.employeeId().toString(), buildingId);
            if (!employee.getBuilding().getId().equals(building.getId())) {
                throw new NotFoundException("Employee not found in this building");
            }
            taskToUpdate.setEmployee(employee);
        }

        mapper.updateTaskFromRequestDTO(dto, taskToUpdate);
        taskToUpdate.setBuilding(building);

        return taskRepository.save(taskToUpdate);
    }

    public Task findByIdAndBuilding(String id, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        return taskRepository.findByIdAndBuilding(UUID.fromString(id), building)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    public void delete(String id, String buildingId) {
        var task = findByIdAndBuilding(id, buildingId);
        taskRepository.delete(task);
    }

    public PageResponseDTO<TaskResponseDTO> findAllWithFilters(TaskFilterDTO filter, String buildingId, int page, int size, String sortBy, String sortDirection) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Task> spec = TaskSpecification.withFilters(filter, currentUser)
                .and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("building"), building));

        Page<Task> taskPage = taskRepository.findAll(spec, pageable);

        List<TaskResponseDTO> content = taskPage.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();

        return PageResponseDTO.<TaskResponseDTO>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .hasNext(taskPage.hasNext())
                .hasPrevious(taskPage.hasPrevious())
                .build();
    }

    @Transactional
    public Task updateStatus(String id, ActivityStatus status, String buildingId, String reason) {
        Task task = findByIdAndBuilding(id, buildingId);
        task.setActivityStatus(status);
        task.setReason(reason);
        return taskRepository.save(task);
    }
}



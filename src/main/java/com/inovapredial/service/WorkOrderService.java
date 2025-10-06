package com.inovapredial.service;

import com.inovapredial.dto.WorkOrderFilterDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.requests.WorkOrderRequestDTO;
import com.inovapredial.dto.responses.WorkOrderResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.WorkOrderMapper;
import com.inovapredial.model.Building;
import com.inovapredial.model.Employee;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.OwnUser;
import com.inovapredial.model.WorkOrder;
import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.repository.WorkOrderRepository;
import com.inovapredial.specification.WorkOrderSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderMapper mapper;
    private final WorkOrderRepository workOrderRepository;
    private final BuildingService buildingService;
    private final EquipmentService equipmentService;
    private final EmployeeService employeeService;
    private final SecurityContextService securityContextService;

    @Transactional
    public WorkOrder create(WorkOrderRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        // Buscar Equipment pelo ID fornecido no request
        Equipment equipment = equipmentService.findByIdAndBuilding(dto.equipmentId().toString(), buildingId);
        if (!equipment.getBuilding().getId().equals(building.getId())) {
            throw new NotFoundException("Equipment not found in this building");
        }
        var toSave = mapper.toEntity(dto);
        // Buscar Employee pelo ID fornecido no request
        if (dto.employeeId() != null) {
            Employee employee = employeeService.findByIdAndBuilding(dto.employeeId().toString(), buildingId);
            if (!employee.getBuilding().getId().equals(building.getId())) {
                throw new NotFoundException("Employee not found in this building");
            }
            toSave.setEmployee(employee);
        }

        toSave.setEquipment(equipment);

        toSave.setBuilding(building);

        if (toSave.getOpeningDate() == null) {
            toSave.setOpeningDate(LocalDateTime.now());
        }

        if(toSave.getActivityStatus() == null) {
            toSave.setActivityStatus(ActivityStatus.OPEN);
        }

        return workOrderRepository.save(toSave);
    }

    @Transactional
    public WorkOrder update(String id, WorkOrderRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);
        
        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        var workOrderToUpdate = findByIdAndBuilding(id, buildingId);

        // Se equipmentId foi fornecido, buscar nova equipment
        if (dto.equipmentId() != null) {
            Equipment equipment = equipmentService.findByIdAndBuilding(dto.equipmentId().toString(), buildingId);
            if (!equipment.getBuilding().getId().equals(building.getId())) {
                throw new NotFoundException("Equipment not found in this building");
            }
            workOrderToUpdate.setEquipment(equipment);
        }

        // Se employeeId foi fornecido, buscar novo employee
        if (dto.employeeId() != null) {
            Employee employee = employeeService.findByIdAndBuilding(dto.employeeId().toString(), buildingId);
            if (!employee.getBuilding().getId().equals(building.getId())) {
                throw new NotFoundException("Employee not found in this building");
            }
            workOrderToUpdate.setEmployee(employee);
        }

        mapper.updateWorkOrderFromRequestDTO(dto, workOrderToUpdate);

        return workOrderRepository.save(workOrderToUpdate);
    }

    public WorkOrder findByIdAndBuilding(String id, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        return workOrderRepository.findByIdAndBuilding(UUID.fromString(id), building)
                .orElseThrow(() -> new NotFoundException("WorkOrder not found"));
    }

    public void delete(String id, String buildingId) {
        var workOrder = findByIdAndBuilding(id, buildingId);
        workOrderRepository.delete(workOrder);
    }

    public PageResponseDTO<WorkOrderResponseDTO> findAllWithFilters(WorkOrderFilterDTO filter, String buildingId, int page, int size, String sortBy, String sortDirection) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Criar specification que inclui filtro por building
        Specification<WorkOrder> spec = WorkOrderSpecification.withFilters(filter, currentUser)
                .and((root, query, criteriaBuilder) -> 
                    criteriaBuilder.equal(root.get("building"), building));

        Page<WorkOrder> workOrderPage = workOrderRepository.findAll(spec, pageable);
        
        List<WorkOrderResponseDTO> content = workOrderPage.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();
        
        return PageResponseDTO.<WorkOrderResponseDTO>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(workOrderPage.getTotalElements())
                .totalPages(workOrderPage.getTotalPages())
                .hasNext(workOrderPage.hasNext())
                .hasPrevious(workOrderPage.hasPrevious())
                .build();
    }
}

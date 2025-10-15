package com.inovapredial.service;

import com.inovapredial.dto.WorkOrderFilterDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.requests.WorkOrderRequestDTO;
import com.inovapredial.dto.requests.WorkOrderInventoryRequestDTO;
import com.inovapredial.dto.responses.WorkOrderResponseDTO;
import com.inovapredial.dto.responses.WorkOrderInventoryResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.exceptions.InsufficientStockException;
import com.inovapredial.mapper.WorkOrderMapper;
import com.inovapredial.model.Building;
import com.inovapredial.model.Employee;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.Inventory;
import com.inovapredial.model.OwnUser;
import com.inovapredial.model.WorkOrder;
import com.inovapredial.model.WorkOrderInventory;
import com.inovapredial.model.WorkOrderInventoryId;
import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.model.enums.EquipmentStatus;
import com.inovapredial.repository.WorkOrderRepository;
import com.inovapredial.repository.WorkOrderInventoryRepository;
import com.inovapredial.repository.EquipmentRepository;
import com.inovapredial.specification.WorkOrderSpecification;
import com.inovapredial.validator.WorkOrderValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkOrderService {

    private final WorkOrderMapper mapper;
    private final WorkOrderRepository workOrderRepository;
    private final WorkOrderInventoryRepository workOrderInventoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final BuildingService buildingService;
    private final EquipmentService equipmentService;
    private final EmployeeService employeeService;
    private final InventoryService inventoryService;
    private final SecurityContextService securityContextService;
    private final WorkOrderValidator workOrderValidator;

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

        // Validar se já existe ordem de serviço em aberto para este equipamento
        workOrderValidator.validateWorkOrderCreation(equipment, building);
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

        // Alterar status do equipamento para EM_MANUTENCAO quando ordem for aberta
        equipment.setEquipmentStatus(EquipmentStatus.UNDER_MAINTENANCE);
        equipmentRepository.save(equipment);

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

        // Validar conclusão da ordem de serviço se o status foi alterado para COMPLETED
        if (workOrderToUpdate.getActivityStatus() == ActivityStatus.COMPLETED) {
            workOrderValidator.validateWorkOrderCompletion(workOrderToUpdate);
            
            // Definir data de fechamento se não estiver definida
            if (workOrderToUpdate.getClosingDate() == null) {
                workOrderToUpdate.setClosingDate(LocalDateTime.now());
            }
            
            // Alterar status do equipamento para ATIVO quando ordem for concluída
            Equipment equipment = workOrderToUpdate.getEquipment();
            equipment.setEquipmentStatus(EquipmentStatus.ACTIVE);
            equipmentRepository.save(equipment);
        } else if (workOrderToUpdate.getActivityStatus() == ActivityStatus.CANCELLED) {
            // Se a ordem for cancelada, alterar status do equipamento para ATIVO
            Equipment equipment = workOrderToUpdate.getEquipment();
            equipment.setEquipmentStatus(EquipmentStatus.ACTIVE);
            equipmentRepository.save(equipment);

        }

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

    @Transactional
    public WorkOrderInventoryResponseDTO addInventoryItem(String workOrderId, WorkOrderInventoryRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        // Buscar a ordem de serviço
        WorkOrder workOrder = findByIdAndBuilding(workOrderId, buildingId);

        // Buscar o item do inventário
        Inventory inventory = inventoryService.findByIdAndBuilding(dto.inventoryId(), buildingId);

        // Verificar disponibilidade no estoque usando o método do InventoryService
        Integer availableStock = inventoryService.getAvailableStock(dto.inventoryId(), buildingId);
        if (availableStock < dto.quantity()) {
            throw new InsufficientStockException(
                String.format("Estoque insuficiente. Disponível: %d, Solicitado: %d", 
                    availableStock, dto.quantity())
            );
        }

        // Verificar se o item já existe na ordem de serviço
        WorkOrderInventoryId id = new WorkOrderInventoryId();
        id.setWorkOrderId(UUID.fromString(workOrderId));
        id.setInventoryId(UUID.fromString(dto.inventoryId()));

        WorkOrderInventory existingItem = workOrderInventoryRepository.findById(id).orElse(null);

        if (existingItem != null) {
            // Atualizar quantidade existente
            int newQuantity = existingItem.getQuantity() + dto.quantity();

            
            // Dar baixa no estoque apenas da quantidade adicional
            inventoryService.reduceStock(dto.inventoryId(), dto.quantity(), buildingId);
            
            existingItem.setQuantity(newQuantity);
            existingItem.setTotalCost(inventory.getCost().multiply(BigDecimal.valueOf(newQuantity)));
            existingItem.setOutputDate(LocalDateTime.now());
            
            workOrderInventoryRepository.save(existingItem);
            
            // Atualizar total da ordem de serviço
            updateWorkOrderTotalCost(workOrder);
            
            return new WorkOrderInventoryResponseDTO(
                dto.inventoryId(),
                inventory.getName(),
                existingItem.getQuantity(),
                inventory.getCost(),
                existingItem.getTotalCost(),
                existingItem.getOutputDate()
            );
        } else {
            // Dar baixa no estoque
            inventoryService.reduceStock(dto.inventoryId(), dto.quantity(), buildingId);
            
            // Criar novo item na ordem de serviço
            BigDecimal totalCost = inventory.getCost().multiply(BigDecimal.valueOf(dto.quantity()));
            
            WorkOrderInventory newItem = WorkOrderInventory.builder()
                .id(id)
                .workOrder(workOrder)
                .inventory(inventory)
                .quantity(dto.quantity())
                .totalCost(totalCost)
                .outputDate(LocalDateTime.now())
                .build();
            
            workOrderInventoryRepository.save(newItem);
            
            // Atualizar total da ordem de serviço
            updateWorkOrderTotalCost(workOrder);
            
            return new WorkOrderInventoryResponseDTO(
                dto.inventoryId(),
                inventory.getName(),
                newItem.getQuantity(),
                inventory.getCost(),
                newItem.getTotalCost(),
                newItem.getOutputDate()
            );
        }
    }

    @Transactional
    public void removeInventoryItem(String workOrderId, String inventoryId, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        // Buscar a ordem de serviço
        WorkOrder workOrder = findByIdAndBuilding(workOrderId, buildingId);

        // Verificar se o item existe na ordem de serviço
        WorkOrderInventoryId id = new WorkOrderInventoryId();
        id.setWorkOrderId(UUID.fromString(workOrderId));
        id.setInventoryId(UUID.fromString(inventoryId));

        WorkOrderInventory item = workOrderInventoryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Item não encontrado na ordem de serviço"));

        // Restaurar o estoque antes de remover o item
        inventoryService.restoreStock(inventoryId, item.getQuantity(), buildingId);

        // Remover o item
        workOrderInventoryRepository.delete(item);

        // Atualizar total da ordem de serviço
        updateWorkOrderTotalCost(workOrder);
    }

    public List<WorkOrderInventoryResponseDTO> getWorkOrderInventory(String workOrderId, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        // Buscar a ordem de serviço
        WorkOrder workOrder = findByIdAndBuilding(workOrderId, buildingId);

        // Buscar todos os itens da ordem de serviço
        List<WorkOrderInventory> items = workOrderInventoryRepository.findByWorkOrder(workOrder);

        return items.stream()
            .map(item -> new WorkOrderInventoryResponseDTO(
                item.getInventory().getId().toString(),
                item.getInventory().getName(),
                item.getQuantity(),
                item.getInventory().getCost(),
                item.getTotalCost(),
                item.getOutputDate()
            ))
            .toList();
    }

    private void updateWorkOrderTotalCost(WorkOrder workOrder) {
        List<WorkOrderInventory> items = workOrderInventoryRepository.findByWorkOrder(workOrder);
        
        BigDecimal totalCost = items.stream()
            .map(WorkOrderInventory::getTotalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        workOrder.setTotalCost(totalCost);
        workOrderRepository.save(workOrder);
    }
}

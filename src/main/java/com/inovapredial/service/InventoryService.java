package com.inovapredial.service;

import com.inovapredial.dto.InventoryFilterDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.requests.InventoryRequestDTO;
import com.inovapredial.dto.responses.InventoryResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.InventoryMapper;
import com.inovapredial.model.Building;
import com.inovapredial.model.Employee;
import com.inovapredial.model.Inventory;
import com.inovapredial.model.OwnUser;
import com.inovapredial.repository.EmployeeRepository;
import com.inovapredial.repository.InventoryRepository;
import com.inovapredial.specification.InventorySpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryMapper mapper;
    private final InventoryRepository inventoryRepository;
    private final EmployeeService  employeeService;
    private final BuildingService buildingService;
    private final SecurityContextService securityContextService;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Inventory create(InventoryRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        var toSave = mapper.toEntity(dto);
        toSave.setBuilding(building);
        
        // Se employeeId foi fornecido, buscar o funcionário
        if (dto.employeeId() != null) {
            Employee employee = employeeService.findByIdAndBuilding(dto.employeeId().toString(), buildingId);
            toSave.setEmployee(employee);
        }

        return inventoryRepository.save(toSave);
    }

    @Transactional
    public Inventory update(String id, InventoryRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);
        
        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        var inventoryToUpdate = findByIdAndBuilding(id, buildingId);

        mapper.updateInventoryFromRequestDTO(dto, inventoryToUpdate);
        
        // Se employeeId foi fornecido, buscar o funcionário
        if (dto.employeeId() != null) {
            Employee employee = employeeService.findByIdAndBuilding(dto.employeeId().toString(), buildingId);
            inventoryToUpdate.setEmployee(employee);
        } else {
            inventoryToUpdate.setEmployee(null);
        }

        return inventoryRepository.save(inventoryToUpdate);
    }

    public Inventory findByIdAndBuilding(String id, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        return inventoryRepository.findByIdAndBuilding(UUID.fromString(id), building)
                .orElseThrow(() -> new NotFoundException("Inventory not found"));
    }

    public void delete(String id, String buildingId) {
        var inventory = findByIdAndBuilding(id, buildingId);
        inventoryRepository.delete(inventory);
    }

    public PageResponseDTO<InventoryResponseDTO> findAllWithFilters(InventoryFilterDTO filter, String buildingId, int page, int size, String sortBy, String sortDirection) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Criar specification que inclui filtro por building
        Specification<Inventory> spec = InventorySpecification.withFilters(filter, currentUser)
                .and((root, query, criteriaBuilder) -> 
                    criteriaBuilder.equal(root.get("building"), building));

        Page<Inventory> inventoryPage = inventoryRepository.findAll(spec, pageable);
        
        List<InventoryResponseDTO> content = inventoryPage.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();
        
        return PageResponseDTO.<InventoryResponseDTO>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(inventoryPage.getTotalElements())
                .totalPages(inventoryPage.getTotalPages())
                .hasNext(inventoryPage.hasNext())
                .hasPrevious(inventoryPage.hasPrevious())
                .build();
    }
}


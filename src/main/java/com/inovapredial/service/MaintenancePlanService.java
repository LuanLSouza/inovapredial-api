package com.inovapredial.service;

import com.inovapredial.dto.MaintenancePlanFilterDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.requests.MaintenancePlanRequestDTO;
import com.inovapredial.dto.responses.MaintenancePlanResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.MaintenancePlanMapper;
import com.inovapredial.model.Building;
import com.inovapredial.model.MaintenancePlan;
import com.inovapredial.model.OwnUser;
import com.inovapredial.repository.MaintenancePlanRepository;
import com.inovapredial.specification.MaintenancePlanSpecification;
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
public class MaintenancePlanService {

    private final MaintenancePlanMapper mapper;
    private final MaintenancePlanRepository maintenancePlanRepository;
    private final BuildingService buildingService;
    private final SecurityContextService securityContextService;

    @Transactional
    public MaintenancePlan create(MaintenancePlanRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        var toSave = mapper.toEntity(dto);
        toSave.setOwnUser(currentUser);
        toSave.setBuilding(building);

        return maintenancePlanRepository.save(toSave);
    }

    @Transactional
    public MaintenancePlan update(String id, MaintenancePlanRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);
        
        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        var maintenancePlanToUpdate = findByIdAndBuilding(id, buildingId);

        mapper.updateMaintenancePlanFromRequestDTO(dto, maintenancePlanToUpdate);

        return maintenancePlanRepository.save(maintenancePlanToUpdate);
    }

    public MaintenancePlan findByIdAndBuilding(String id, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        return maintenancePlanRepository.findByIdAndBuilding(UUID.fromString(id), building)
                .orElseThrow(() -> new NotFoundException("Maintenance plan not found"));
    }

    public void delete(String id, String buildingId) {
        var maintenancePlan = findByIdAndBuilding(id, buildingId);
        maintenancePlanRepository.delete(maintenancePlan);
    }

    public PageResponseDTO<MaintenancePlanResponseDTO> findAllWithFilters(MaintenancePlanFilterDTO filter, String buildingId, int page, int size, String sortBy, String sortDirection) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Criar specification que inclui filtro por building
        Specification<MaintenancePlan> spec = MaintenancePlanSpecification.withFilters(filter, currentUser)
                .and((root, query, criteriaBuilder) -> 
                    criteriaBuilder.equal(root.get("building"), building));

        Page<MaintenancePlan> maintenancePlanPage = maintenancePlanRepository.findAll(spec, pageable);
        
        List<MaintenancePlanResponseDTO> content = maintenancePlanPage.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();
        
        return PageResponseDTO.<MaintenancePlanResponseDTO>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(maintenancePlanPage.getTotalElements())
                .totalPages(maintenancePlanPage.getTotalPages())
                .hasNext(maintenancePlanPage.hasNext())
                .hasPrevious(maintenancePlanPage.hasPrevious())
                .build();
    }
}

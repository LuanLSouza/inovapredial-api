package com.inovapredial.service;

import com.inovapredial.dto.EquipmentFilterDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.requests.EquipmentRequestDTO;
import com.inovapredial.dto.responses.EquipmentResponseDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.EquipmentMapper;
import com.inovapredial.model.Building;
import com.inovapredial.model.Calendar;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.OwnUser;
import com.inovapredial.repository.CalendarRepository;
import com.inovapredial.repository.EquipmentRepository;
import com.inovapredial.specification.EquipmentSpecification;
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
public class EquipmentService {

    private final EquipmentMapper mapper;
    private final EquipmentRepository equipmentRepository;
    private final BuildingService buildingService;
    private final CalendarRepository calendarRepository;
    private final SecurityContextService securityContextService;

    @Transactional
    public Equipment create(EquipmentRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        var toSave = mapper.toEntity(dto);
        toSave.setOwnUser(currentUser);
        toSave.setBuilding(building);

        // Se calendar foi fornecido, criar ou atualizar o calendar
        if (dto.calendar() != null) {
            Calendar calendar;
            if (dto.calendar().id() != null) {
                // Se tem ID, buscar existente ou criar novo
                calendar = calendarRepository.findById(dto.calendar().id())
                        .orElseGet(() -> {
                            Calendar newCalendar = mapper.toEntity(dto.calendar());
                            return calendarRepository.save(newCalendar);
                        });
            } else {
                // Se não tem ID, criar novo
                calendar = mapper.toEntity(dto.calendar());
                calendar = calendarRepository.save(calendar);
            }
            toSave.setCalendar(calendar);
        }

        return equipmentRepository.save(toSave);
    }

    @Transactional
    public Equipment update(String id, EquipmentRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);
        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        var equipmentToUpdate = findByIdAndBuilding(id, buildingId);

        mapper.updateEquipmentFromRequestDTO(dto, equipmentToUpdate);

        // Se calendar foi fornecido, criar ou atualizar o calendar
        if (dto.calendar() != null) {
            Calendar calendar;
            if (dto.calendar().id() != null) {
                // Se tem ID, buscar existente ou criar novo
                calendar = calendarRepository.findById(dto.calendar().id())
                        .orElseGet(() -> {
                            Calendar newCalendar = mapper.toEntity(dto.calendar());
                            return calendarRepository.save(newCalendar);
                        });
            } else {
                // Se não tem ID, criar novo
                calendar = mapper.toEntity(dto.calendar());
                calendar = calendarRepository.save(calendar);
            }
            equipmentToUpdate.setCalendar(calendar);
        } else {
            equipmentToUpdate.setCalendar(null);
        }

        return equipmentRepository.save(equipmentToUpdate);
    }

    public Equipment findByIdAndBuilding(String id, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        return equipmentRepository.findByIdAndBuilding(UUID.fromString(id), building)
                .orElseThrow(() -> new NotFoundException("Equipment not found"));
    }

    public void delete(String id, String buildingId) {
        var equipment = findByIdAndBuilding(id, buildingId);
        equipmentRepository.delete(equipment);
    }

    public PageResponseDTO<EquipmentResponseDTO> findAllWithFilters(EquipmentFilterDTO filter, String buildingId, int page, int size, String sortBy, String sortDirection) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Criar specification que inclui filtro por building
        Specification<Equipment> spec = EquipmentSpecification.withFilters(filter, currentUser)
                .and((root, query, criteriaBuilder) -> 
                    criteriaBuilder.equal(root.get("building"), building));

        Page<Equipment> equipmentPage = equipmentRepository.findAll(spec, pageable);
        
        List<EquipmentResponseDTO> content = equipmentPage.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();
        
        return PageResponseDTO.<EquipmentResponseDTO>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(equipmentPage.getTotalElements())
                .totalPages(equipmentPage.getTotalPages())
                .hasNext(equipmentPage.hasNext())
                .hasPrevious(equipmentPage.hasPrevious())
                .build();
    }
}

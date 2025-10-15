package com.inovapredial.service;

import com.inovapredial.dto.requests.EquipmentPlanRequestDTO;
import com.inovapredial.dto.requests.EquipmentPlanUpdateRequestDTO;
import com.inovapredial.dto.responses.EquipmentPlanResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.EquipmentPlanMapper;
import com.inovapredial.model.Building;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.EquipmentPlan;
import com.inovapredial.model.EquipmentPlanId;
import com.inovapredial.model.MaintenancePlan;
import com.inovapredial.model.OwnUser;
import com.inovapredial.repository.EquipmentPlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipmentPlanService {

    private final EquipmentPlanRepository equipmentPlanRepository;
    private final EquipmentPlanMapper equipmentPlanMapper;
    private final EquipmentService equipmentService;
    private final MaintenancePlanService maintenancePlanService;
    private final BuildingService buildingService;
    private final SecurityContextService securityContextService;

    @Transactional
    public EquipmentPlanResponseDTO addPlanToEquipment(EquipmentPlanRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        // Buscar equipamento e plano (validar building)
        Equipment equipment = equipmentService.findByIdAndBuilding(dto.equipmentId(), buildingId);
        MaintenancePlan maintenancePlan = maintenancePlanService.findByIdAndBuilding(dto.planId(), buildingId);

        // Criar EquipmentPlanId
        EquipmentPlanId id = new EquipmentPlanId();
        id.setEquipmentId(UUID.fromString(dto.equipmentId()));
        id.setPlanId(UUID.fromString(dto.planId()));

        // Verificar se já existe o relacionamento
        if (equipmentPlanRepository.existsById(id)) {
            throw new IllegalArgumentException("Este plano já está associado ao equipamento");
        }

        // Calcular nextDueDate = startDate + frequencyDays do plano
        LocalDate nextDueDate = dto.startDate().plusDays(maintenancePlan.getFrequencyDays());

        // Criar EquipmentPlan
        EquipmentPlan equipmentPlan = EquipmentPlan.builder()
                .id(id)
                .equipment(equipment)
                .maintenancePlan(maintenancePlan)
                .startDate(dto.startDate())
                .nextDueDate(nextDueDate)
                .realized(false)
                .building(building)
                .build();

        EquipmentPlan saved = equipmentPlanRepository.save(equipmentPlan);
        return equipmentPlanMapper.toResponseDTO(saved);
    }

    @Transactional
    public void removePlanFromEquipment(String equipmentId, String planId, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        // Buscar EquipmentPlan pelo ID composto
        EquipmentPlanId id = new EquipmentPlanId();
        id.setEquipmentId(UUID.fromString(equipmentId));
        id.setPlanId(UUID.fromString(planId));

        EquipmentPlan equipmentPlan = equipmentPlanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Relacionamento equipamento-plano não encontrado"));

        // Verificar se pertence ao building correto
        if (!equipmentPlan.getBuilding().getId().equals(building.getId())) {
            throw new NotFoundException("Relacionamento equipamento-plano não encontrado");
        }

        // Deletar o relacionamento
        equipmentPlanRepository.delete(equipmentPlan);
    }

    @Transactional
    public EquipmentPlanResponseDTO updateRealized(String equipmentId, String planId, 
                                                  EquipmentPlanUpdateRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        // Buscar EquipmentPlan
        EquipmentPlanId id = new EquipmentPlanId();
        id.setEquipmentId(UUID.fromString(equipmentId));
        id.setPlanId(UUID.fromString(planId));

        EquipmentPlan equipmentPlan = equipmentPlanRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Relacionamento equipamento-plano não encontrado"));

        // Verificar se pertence ao building correto
        if (!equipmentPlan.getBuilding().getId().equals(building.getId())) {
            throw new NotFoundException("Relacionamento equipamento-plano não encontrado");
        }

        // Atualizar o campo realized
        equipmentPlan.setRealized(dto.realized());

        // Se realized = true: recalcular nextDueDate = nextDueDate anterior + frequencyDays
        if (dto.realized()) {
            LocalDate newNextDueDate = equipmentPlan.getNextDueDate()
                    .plusDays(equipmentPlan.getMaintenancePlan().getFrequencyDays());
            equipmentPlan.setNextDueDate(newNextDueDate);
        }

        EquipmentPlan saved = equipmentPlanRepository.save(equipmentPlan);
        return equipmentPlanMapper.toResponseDTO(saved);
    }

    public List<EquipmentPlanResponseDTO> getEquipmentPlans(String equipmentId, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        // Buscar equipamento
        Equipment equipment = equipmentService.findByIdAndBuilding(equipmentId, buildingId);

        // Buscar todos os planos associados ao equipamento no building
        List<EquipmentPlan> equipmentPlans = equipmentPlanRepository.findByEquipmentAndBuilding(equipment, building);

        return equipmentPlans.stream()
                .map(equipmentPlanMapper::toResponseDTO)
                .toList();
    }
}


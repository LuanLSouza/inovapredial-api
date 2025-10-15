package com.inovapredial.validator;

import com.inovapredial.exceptions.DeletionBlockedException;
import com.inovapredial.repository.BuildingRepository;
import com.inovapredial.repository.EmployeeRepository;
import com.inovapredial.repository.EquipmentPlanRepository;
import com.inovapredial.repository.EquipmentRepository;
import com.inovapredial.repository.InventoryRepository;
import com.inovapredial.repository.MaintenancePlanRepository;
import com.inovapredial.repository.TaskRepository;
import com.inovapredial.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BuildingValidator {

    private final BuildingRepository buildingRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final WorkOrderRepository workOrderRepository;
    private final EquipmentRepository equipmentRepository;
    private final InventoryRepository inventoryRepository;
    private final MaintenancePlanRepository maintenancePlanRepository;
    private final EquipmentPlanRepository equipmentPlanRepository;

    public void validateBuildingDeletion(UUID buildingId) {

        if (!buildingRepository.existsById(buildingId)) {
            return;
        }

        if (employeeRepository.countByBuildingId(buildingId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }

        if (taskRepository.countByBuildingId(buildingId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }

        if (workOrderRepository.countByBuildingId(buildingId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }

        if (equipmentRepository.countByBuildingId(buildingId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }

        if (inventoryRepository.countByBuildingId(buildingId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }

        if (maintenancePlanRepository.countByBuildingId(buildingId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }

        if (equipmentPlanRepository.countByBuildingId(buildingId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }
    }
}

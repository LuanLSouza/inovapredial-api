package com.inovapredial.repository;

import com.inovapredial.model.Building;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.EquipmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipmentPlanRepository extends JpaRepository<EquipmentPlan, com.inovapredial.model.EquipmentPlanId> {
    
    List<EquipmentPlan> findByEquipment(Equipment equipment);
    
    Optional<EquipmentPlan> findByEquipmentIdAndMaintenancePlanId(UUID equipmentId, UUID planId);
    
    List<EquipmentPlan> findByEquipmentAndBuilding(Equipment equipment, Building building);
    
    long countByBuildingId(UUID buildingId);
    
    long countByEquipmentId(UUID equipmentId);
}

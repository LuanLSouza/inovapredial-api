package com.inovapredial.repository;

import com.inovapredial.model.Building;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.WorkOrder;
import com.inovapredial.model.enums.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID>, JpaSpecificationExecutor<WorkOrder> {
    
    Optional<WorkOrder> findByIdAndBuilding(UUID id, Building building);
    
    boolean existsByEquipmentAndBuildingAndActivityStatus(Equipment equipment, Building building, ActivityStatus activityStatus);
    
    long countByBuildingId(UUID buildingId);
    
    long countByEquipmentId(UUID equipmentId);
    
    long countByEmployeeId(UUID employeeId);
}

package com.inovapredial.repository;

import com.inovapredial.model.Building;
import com.inovapredial.model.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID>, JpaSpecificationExecutor<WorkOrder> {
    
    Optional<WorkOrder> findByIdAndBuilding(UUID id, Building building);
    
    long countByBuildingId(UUID buildingId);
}

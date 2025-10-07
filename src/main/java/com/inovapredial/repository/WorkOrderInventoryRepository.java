package com.inovapredial.repository;

import com.inovapredial.model.WorkOrder;
import com.inovapredial.model.WorkOrderInventory;
import com.inovapredial.model.WorkOrderInventoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkOrderInventoryRepository extends JpaRepository<WorkOrderInventory, WorkOrderInventoryId> {
    
    List<WorkOrderInventory> findByWorkOrder(WorkOrder workOrder);
    
    Optional<WorkOrderInventory> findByWorkOrderIdAndInventoryId(UUID workOrderId, UUID inventoryId);
    
    @Query("SELECT woi FROM WorkOrderInventory woi WHERE woi.workOrder.id = :workOrderId")
    List<WorkOrderInventory> findByWorkOrderId(@Param("workOrderId") UUID workOrderId);
    
    void deleteByWorkOrderIdAndInventoryId(UUID workOrderId, UUID inventoryId);
}

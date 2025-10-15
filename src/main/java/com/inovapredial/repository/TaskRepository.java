package com.inovapredial.repository;

import com.inovapredial.model.Building;
import com.inovapredial.model.Task;
import com.inovapredial.model.WorkOrder;
import com.inovapredial.model.enums.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    Optional<Task> findByIdAndBuilding(UUID id, Building building);

    List<Task> findAllByWorkOrderAndBuilding(WorkOrder workOrder, Building building);
    
    List<Task> findAllByWorkOrderId(UUID workOrderId);
    
    long countByWorkOrderId(UUID workOrderId);
    
    long countByBuildingId(UUID buildingId);
    
    long countByEmployeeId(UUID employeeId);
}



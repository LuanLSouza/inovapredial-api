package com.inovapredial.repository;

import com.inovapredial.model.Building;
import com.inovapredial.model.MaintenancePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface MaintenancePlanRepository extends JpaRepository<MaintenancePlan, UUID>, JpaSpecificationExecutor<MaintenancePlan> {
    
    Optional<MaintenancePlan> findByIdAndBuilding(UUID id, Building building);
}

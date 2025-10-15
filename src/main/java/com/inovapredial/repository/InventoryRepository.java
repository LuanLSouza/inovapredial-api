package com.inovapredial.repository;

import com.inovapredial.model.Building;
import com.inovapredial.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Inventory> {
    
    Optional<Inventory> findByIdAndBuilding(UUID id, Building building);
    
    long countByBuildingId(UUID buildingId);
}

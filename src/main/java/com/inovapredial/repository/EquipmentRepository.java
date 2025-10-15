package com.inovapredial.repository;

import com.inovapredial.model.Building;
import com.inovapredial.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID>, JpaSpecificationExecutor<Equipment> {
    
    Optional<Equipment> findByIdAndBuilding(UUID id, Building building);
    
    long countByBuildingId(UUID buildingId);
}

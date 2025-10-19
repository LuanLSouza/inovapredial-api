package com.inovapredial.repository;

import com.inovapredial.dto.projection.EquipmentFailureProjection;
import com.inovapredial.model.Building;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.enums.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID>, JpaSpecificationExecutor<Equipment> {
    
    Optional<Equipment> findByIdAndBuilding(UUID id, Building building);
    
    long countByBuildingId(UUID buildingId);

    // Contagens por status
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.building.id = :buildingId " +
           "AND e.equipmentStatus = :status")
    Long countByStatusAndBuilding(@Param("buildingId") UUID buildingId,
                                 @Param("status") EquipmentStatus status);

    // Contagem de equipamentos críticos
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.building.id = :buildingId " +
           "AND e.criticality = 'HIGH'")
    Long countCriticalEquipmentsByBuilding(@Param("buildingId") UUID buildingId);

    // MTBF - Métricas de falhas de equipamentos (versão simplificada)
    @Query(value = """
        SELECT e.id as equipmentId, e.description as equipmentName, e.identification as equipmentIdentification,
               e.criticality as criticality, COUNT(wo.id) as failureCount,
               CASE 
                   WHEN COUNT(wo.id) > 1 THEN 
                       EXTRACT(EPOCH FROM (MAX(wo.opening_date) - MIN(wo.opening_date)))/86400 / (COUNT(wo.id) - 1)
                   ELSE NULL 
               END as averageDaysBetweenFailures,
               MAX(wo.opening_date) as lastFailureDate
        FROM equipment e
        LEFT JOIN work_order wo ON e.id = wo.equipment_id
        AND wo.maintenance_type = 'CORRECTIVE'
        AND wo.activity_status = 'COMPLETED'
        AND wo.opening_date >= COALESCE(:startDate, CAST('1900-01-01' AS timestamp))
        AND wo.opening_date <= COALESCE(:endDate, CAST('2100-12-31' AS timestamp))
        WHERE e.building_id = :buildingId
        GROUP BY e.id, e.description, e.identification, e.criticality
        HAVING COUNT(wo.id) > 0
        ORDER BY failureCount DESC
        """, nativeQuery = true)
    List<EquipmentFailureProjection> findEquipmentFailureMetrics(@Param("buildingId") UUID buildingId,
                                                                 @Param("startDate") LocalDateTime startDate,
                                                                 @Param("endDate") LocalDateTime endDate);
}

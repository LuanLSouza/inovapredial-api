package com.inovapredial.repository;

import com.inovapredial.dto.projection.MaintenanceTypeMetricProjection;
import com.inovapredial.dto.projection.MonthlyCostProjection;
import com.inovapredial.model.Building;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.WorkOrder;
import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.model.enums.MaintenanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID>, JpaSpecificationExecutor<WorkOrder> {
    
    Optional<WorkOrder> findByIdAndBuilding(UUID id, Building building);
    
    boolean existsByEquipmentAndBuildingAndActivityStatus(Equipment equipment, Building building, ActivityStatus activityStatus);
    
    long countByBuildingId(UUID buildingId);
    
    // Contagem com filtro de data
    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.building.id = :buildingId " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    Long countByBuildingIdAndDateRange(@Param("buildingId") UUID buildingId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    long countByEquipmentId(UUID equipmentId);
    
    long countByEmployeeId(UUID employeeId);

    // Métricas de custos
    @Query("SELECT COALESCE(SUM(wo.totalCost), 0) FROM WorkOrder wo WHERE wo.building.id = :buildingId " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    BigDecimal sumTotalCostByBuildingAndDateRange(@Param("buildingId") UUID buildingId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(wo.totalCost), 0) FROM WorkOrder wo WHERE wo.building.id = :buildingId " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime)) " +
           "AND (:equipmentId IS NULL OR wo.equipment.id = :equipmentId) " +
           "AND (:employeeId IS NULL OR wo.employee.id = :employeeId) " +
           "AND (:maintenanceType IS NULL OR wo.maintenanceType = :maintenanceType)")
    BigDecimal sumTotalCostByFilters(@Param("buildingId") UUID buildingId,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("equipmentId") UUID equipmentId,
                                    @Param("employeeId") UUID employeeId,
                                    @Param("maintenanceType") MaintenanceType maintenanceType);

    // Métricas temporais
    @Query("SELECT EXTRACT(YEAR FROM wo.openingDate) as year, EXTRACT(MONTH FROM wo.openingDate) as month, " +
           "COALESCE(SUM(wo.totalCost), 0) as totalCost, COUNT(wo) as workOrderCount " +
           "FROM WorkOrder wo WHERE wo.building.id = :buildingId " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime)) " +
           "GROUP BY EXTRACT(YEAR FROM wo.openingDate), EXTRACT(MONTH FROM wo.openingDate) " +
           "ORDER BY year, month")
    List<MonthlyCostProjection> findMonthlyCostsByBuildingAndDateRange(@Param("buildingId") UUID buildingId,
                                                                        @Param("startDate") LocalDateTime startDate,
                                                                        @Param("endDate") LocalDateTime endDate);

    // Contagens por tipo de manutenção
    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.building.id = :buildingId " +
           "AND wo.maintenanceType = :maintenanceType " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    Long countByMaintenanceTypeAndFilters(@Param("buildingId") UUID buildingId,
                                          @Param("maintenanceType") MaintenanceType maintenanceType,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    // Métricas por tipo de manutenção
    @Query("SELECT wo.maintenanceType as maintenanceType, COUNT(wo) as count, " +
           "COALESCE(SUM(wo.totalCost), 0) as totalCost, " +
           "COALESCE(AVG(wo.totalCost), 0) as averageCost " +
           "FROM WorkOrder wo WHERE wo.building.id = :buildingId " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime)) " +
           "GROUP BY wo.maintenanceType")
    List<MaintenanceTypeMetricProjection> findMaintenanceTypeMetrics(@Param("buildingId") UUID buildingId,
                                                                    @Param("startDate") LocalDateTime startDate,
                                                                    @Param("endDate") LocalDateTime endDate);

    // Contagens por status
    @Query("SELECT COUNT(wo) FROM WorkOrder wo WHERE wo.building.id = :buildingId " +
           "AND wo.activityStatus = :status " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    Long countByStatusAndFilters(@Param("buildingId") UUID buildingId,
                                 @Param("status") ActivityStatus status,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    // Tempo médio de conclusão
    @Query("SELECT AVG(EXTRACT(EPOCH FROM wo.closingDate) - EXTRACT(EPOCH FROM wo.openingDate))/3600 " +
           "FROM WorkOrder wo WHERE wo.building.id = :buildingId " +
           "AND wo.activityStatus = 'COMPLETED' " +
           "AND wo.closingDate IS NOT NULL " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    Double calculateAverageCompletionTimeHours(@Param("buildingId") UUID buildingId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
}

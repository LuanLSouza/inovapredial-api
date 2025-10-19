package com.inovapredial.repository;

import com.inovapredial.dto.projection.MonthlyCostProjection;
import com.inovapredial.model.Building;
import com.inovapredial.model.Task;
import com.inovapredial.model.WorkOrder;
import com.inovapredial.model.enums.ActivityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    Optional<Task> findByIdAndBuilding(UUID id, Building building);

    List<Task> findAllByWorkOrderAndBuilding(WorkOrder workOrder, Building building);
    
    List<Task> findAllByWorkOrderId(UUID workOrderId);
    
    long countByWorkOrderId(UUID workOrderId);
    
    long countByBuildingId(UUID buildingId);
    
    // Contagem com filtro de data
    @Query("SELECT COUNT(t) FROM Task t WHERE t.building.id = :buildingId " +
           "AND t.startDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND t.startDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    Long countByBuildingIdAndDateRange(@Param("buildingId") UUID buildingId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    long countByEmployeeId(UUID employeeId);

    // Métricas de custos de tarefas
    @Query("SELECT COALESCE(SUM(t.cost), 0) FROM Task t WHERE t.building.id = :buildingId " +
           "AND t.startDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND t.startDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    BigDecimal sumTotalCostByBuildingAndDateRange(@Param("buildingId") UUID buildingId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.cost), 0) FROM Task t WHERE t.building.id = :buildingId " +
           "AND t.startDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND t.startDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime)) " +
           "AND (:equipmentId IS NULL OR t.workOrder.equipment.id = :equipmentId) " +
           "AND (:employeeId IS NULL OR t.employee.id = :employeeId)")
    BigDecimal sumTotalCostByFilters(@Param("buildingId") UUID buildingId,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("equipmentId") UUID equipmentId,
                                    @Param("employeeId") UUID employeeId);

    // Tempo médio de conclusão de tarefas
    @Query("SELECT AVG(EXTRACT(EPOCH FROM t.endDate) - EXTRACT(EPOCH FROM t.startDate))/3600 " +
           "FROM Task t WHERE t.building.id = :buildingId " +
           "AND t.activityStatus = 'COMPLETED' " +
           "AND t.endDate IS NOT NULL " +
           "AND t.startDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND t.startDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    Double calculateAverageCompletionTimeHours(@Param("buildingId") UUID buildingId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    // Métricas temporais de tarefas
    @Query("SELECT EXTRACT(YEAR FROM t.startDate) as year, EXTRACT(MONTH FROM t.startDate) as month, " +
           "COALESCE(SUM(t.cost), 0) as totalCost, COUNT(t) as taskCount " +
           "FROM Task t WHERE t.building.id = :buildingId " +
           "AND t.startDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND t.startDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime)) " +
           "GROUP BY EXTRACT(YEAR FROM t.startDate), EXTRACT(MONTH FROM t.startDate) " +
           "ORDER BY year, month")
    List<MonthlyCostProjection> findMonthlyTaskCostsByBuildingAndDateRange(@Param("buildingId") UUID buildingId,
                                                                             @Param("startDate") LocalDateTime startDate,
                                                                             @Param("endDate") LocalDateTime endDate);

    // Contagens por status de tarefas
    @Query("SELECT COUNT(t) FROM Task t WHERE t.building.id = :buildingId " +
           "AND t.activityStatus = :status " +
           "AND t.startDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND t.startDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    Long countByStatusAndFilters(@Param("buildingId") UUID buildingId,
                                 @Param("status") ActivityStatus status,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
}



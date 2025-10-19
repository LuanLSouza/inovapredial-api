package com.inovapredial.repository;

import com.inovapredial.dto.projection.LowStockAlertProjection;
import com.inovapredial.dto.projection.TopUsedItemProjection;
import com.inovapredial.model.Building;
import com.inovapredial.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID>, JpaSpecificationExecutor<Inventory> {
    
    Optional<Inventory> findByIdAndBuilding(UUID id, Building building);
    
    long countByBuildingId(UUID buildingId);

    // Itens com estoque baixo
    @Query("SELECT i.id as itemId, i.name as itemName, i.itemType as itemType, " +
           "i.quantity as currentQuantity, i.minimumStock as minimumQuantity, i.cost as unitCost " +
           "FROM Inventory i WHERE i.building.id = :buildingId " +
           "AND i.quantity <= i.minimumStock " +
           "ORDER BY i.quantity ASC")
    List<LowStockAlertProjection> findLowStockItems(@Param("buildingId") UUID buildingId);

    // Itens mais utilizados (apenas os que foram realmente usados no período)
    @Query("SELECT i.id as itemId, i.name as itemName, i.itemType as itemType, " +
           "SUM(woi.quantity) as totalQuantityUsed, " +
           "SUM(woi.totalCost) as totalCost, i.quantity as currentStock " +
           "FROM Inventory i " +
           "INNER JOIN WorkOrderInventory woi ON i.id = woi.inventory.id " +
           "INNER JOIN WorkOrder wo ON woi.workOrder.id = wo.id " +
           "WHERE i.building.id = :buildingId " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime)) " +
           "GROUP BY i.id, i.name, i.itemType, i.quantity " +
           "ORDER BY totalQuantityUsed DESC")
    List<TopUsedItemProjection> findTopUsedItems(@Param("buildingId") UUID buildingId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

    // Custo total com materiais utilizados
    @Query("SELECT COALESCE(SUM(woi.totalCost), 0) " +
           "FROM WorkOrderInventory woi " +
           "JOIN woi.workOrder wo " +
           "WHERE wo.building.id = :buildingId " +
           "AND wo.openingDate >= COALESCE(:startDate, CAST('1900-01-01' AS LocalDateTime)) " +
           "AND wo.openingDate <= COALESCE(:endDate, CAST('2100-12-31' AS LocalDateTime))")
    java.math.BigDecimal sumTotalMaterialCost(@Param("buildingId") UUID buildingId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
}

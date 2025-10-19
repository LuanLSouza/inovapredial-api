package com.inovapredial.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricResponseDTO {

    private GeneralMetricsDTO generalMetrics;
    private WorkOrderMetricsDTO workOrderMetrics;
    private EquipmentMetricsDTO equipmentMetrics;
    private TimeSeriesMetricsDTO timeSeriesMetrics;
    private InventoryMetricsDTO inventoryMetrics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneralMetricsDTO {
        private BigDecimal totalCost;
        private Long totalWorkOrders;
        private Long totalTasks;
        private Long activeEquipments;
        private Long totalEquipments;
        private BigDecimal averageCostPerWorkOrder;
        private BigDecimal averageCostPerTask;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkOrderMetricsDTO {
        private Long totalWorkOrders;
        private Long completedWorkOrders;
        private Long cancelledWorkOrders;
        private Long inProgressWorkOrders;
        private BigDecimal totalCost;
        private BigDecimal averageCompletionTimeHours;
        private Double completionRate;
        private MaintenanceTypeMetricsDTO preventive;
        private MaintenanceTypeMetricsDTO corrective;
        private MaintenanceTypeMetricsDTO predictive;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaintenanceTypeMetricsDTO {
        private Long count;
        private BigDecimal totalCost;
        private BigDecimal averageCost;
        private Double percentageOfTotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquipmentMetricsDTO {
        private Long totalEquipments;
        private Long activeEquipments;
        private Long inactiveEquipments;
        private Long criticalEquipments;
        private List<EquipmentMTBFDTO> equipmentMTBF;
        private EquipmentStatusDistributionDTO statusDistribution;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquipmentMTBFDTO {
        private String equipmentId;
        private String equipmentName;
        private String equipmentIdentification;
        private Long failureCount;
        private Double averageDaysBetweenFailures;
        private String lastFailureDate;
        private String criticality;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquipmentStatusDistributionDTO {
        private Long active;
        private Long inactive;
        private Long maintenance;
        private Long outOfService; // Mantido para compatibilidade futura
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSeriesMetricsDTO {
        private List<MonthlyCostDTO> monthlyCosts;
        private List<MonthlyWorkOrderDTO> monthlyWorkOrders;
        private List<MonthlyTaskDTO> monthlyTasks;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyCostDTO {
        private Integer year;
        private Integer month;
        private BigDecimal totalCost;
        private Long workOrderCount;
        private Long taskCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyWorkOrderDTO {
        private Integer year;
        private Integer month;
        private Long totalCount;
        private Long completedCount;
        private Long cancelledCount;
        private Long inProgressCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyTaskDTO {
        private Integer year;
        private Integer month;
        private Long totalCount;
        private Long completedCount;
        private BigDecimal totalCost;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryMetricsDTO {
        private BigDecimal totalMaterialCost;
        private Long totalItemsUsed;
        private List<TopUsedItemDTO> topUsedItems;
        private List<LowStockAlertDTO> lowStockAlerts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopUsedItemDTO {
        private String itemId;
        private String itemName;
        private String itemType;
        private Long totalQuantityUsed;
        private BigDecimal totalCost;
        private Integer currentStock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockAlertDTO {
        private String itemId;
        private String itemName;
        private String itemType;
        private Integer currentQuantity;
        private Integer minimumQuantity;
        private BigDecimal unitCost;
    }
}

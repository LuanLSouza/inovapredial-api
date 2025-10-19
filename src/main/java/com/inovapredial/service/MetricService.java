package com.inovapredial.service;

import com.inovapredial.dto.MetricFilterDTO;
import com.inovapredial.dto.projection.EquipmentFailureProjection;
import com.inovapredial.dto.projection.LowStockAlertProjection;
import com.inovapredial.dto.projection.MaintenanceTypeMetricProjection;
import com.inovapredial.dto.projection.MonthlyCostProjection;
import com.inovapredial.dto.projection.TopUsedItemProjection;
import com.inovapredial.dto.responses.MetricResponseDTO;
import com.inovapredial.model.Building;
import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.model.enums.EquipmentStatus;
import com.inovapredial.model.enums.MaintenanceType;
import com.inovapredial.repository.EquipmentRepository;
import com.inovapredial.repository.InventoryRepository;
import com.inovapredial.repository.TaskRepository;
import com.inovapredial.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final WorkOrderRepository workOrderRepository;
    private final TaskRepository taskRepository;
    private final EquipmentRepository equipmentRepository;
    private final InventoryRepository inventoryRepository;
    private final BuildingService buildingService;

    public MetricResponseDTO calculateCompleteMetrics(MetricFilterDTO filter) {
        validateFilter(filter);
        
        MetricResponseDTO.MetricResponseDTOBuilder builder = MetricResponseDTO.builder();
        
        builder.generalMetrics(calculateGeneralMetrics(filter))
               .workOrderMetrics(calculateWorkOrderMetrics(filter))
               .equipmentMetrics(calculateEquipmentMetrics(filter))
               .timeSeriesMetrics(calculateTimeSeriesMetrics(filter))
               .inventoryMetrics(calculateInventoryMetrics(filter));
        
        return builder.build();
    }

    public MetricResponseDTO.GeneralMetricsDTO calculateGeneralMetrics(MetricFilterDTO filter) {
        validateFilter(filter);
        
        BigDecimal workOrderCost = workOrderRepository.sumTotalCostByFilters(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate(),
            filter.getEquipmentId(), filter.getEmployeeId(), filter.getMaintenanceType()
        );
        
        BigDecimal taskCost = taskRepository.sumTotalCostByFilters(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate(),
            filter.getEquipmentId(), filter.getEmployeeId()
        );
        
        BigDecimal materialCost = inventoryRepository.sumTotalMaterialCost(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        
        BigDecimal totalCost = workOrderCost.add(taskCost).add(materialCost);
        
        Long totalWorkOrders = workOrderRepository.countByBuildingIdAndDateRange(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        Long totalTasks = taskRepository.countByBuildingIdAndDateRange(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        Long totalEquipments = equipmentRepository.countByBuildingId(filter.getBuildingId());
        Long activeEquipments = equipmentRepository.countByStatusAndBuilding(
            filter.getBuildingId(), EquipmentStatus.ACTIVE
        );
        
        BigDecimal averageCostPerWorkOrder = totalWorkOrders > 0 ? 
            totalCost.divide(BigDecimal.valueOf(totalWorkOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        BigDecimal averageCostPerTask = totalTasks > 0 ? 
            totalCost.divide(BigDecimal.valueOf(totalTasks), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        return MetricResponseDTO.GeneralMetricsDTO.builder()
            .totalCost(totalCost)
            .totalWorkOrders(totalWorkOrders)
            .totalTasks(totalTasks)
            .activeEquipments(activeEquipments)
            .totalEquipments(totalEquipments)
            .averageCostPerWorkOrder(averageCostPerWorkOrder)
            .averageCostPerTask(averageCostPerTask)
            .build();
    }

    public MetricResponseDTO.WorkOrderMetricsDTO calculateWorkOrderMetrics(MetricFilterDTO filter) {
        validateFilter(filter);
        
        Long totalWorkOrders = workOrderRepository.countByBuildingId(filter.getBuildingId());
        Long completedWorkOrders = workOrderRepository.countByStatusAndFilters(
            filter.getBuildingId(), ActivityStatus.COMPLETED, filter.getStartDate(), filter.getEndDate()
        );
        Long cancelledWorkOrders = workOrderRepository.countByStatusAndFilters(
            filter.getBuildingId(), ActivityStatus.CANCELLED, filter.getStartDate(), filter.getEndDate()
        );
        Long inProgressWorkOrders = workOrderRepository.countByStatusAndFilters(
            filter.getBuildingId(), ActivityStatus.IN_PROGRESS, filter.getStartDate(), filter.getEndDate()
        );
        
        BigDecimal totalCost = workOrderRepository.sumTotalCostByFilters(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate(),
            filter.getEquipmentId(), filter.getEmployeeId(), filter.getMaintenanceType()
        );
        
        Double averageCompletionTimeHours = workOrderRepository.calculateAverageCompletionTimeHours(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        
        Double completionRate = totalWorkOrders > 0 ? 
            (completedWorkOrders.doubleValue() / totalWorkOrders.doubleValue()) * 100 : 0.0;
        
        // Métricas por tipo de manutenção
        List<MaintenanceTypeMetricProjection> maintenanceTypeMetrics = workOrderRepository.findMaintenanceTypeMetrics(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        
        Map<MaintenanceType, MaintenanceTypeMetricProjection> metricsMap = maintenanceTypeMetrics.stream()
            .collect(Collectors.toMap(MaintenanceTypeMetricProjection::getMaintenanceType, m -> m));
        
        MetricResponseDTO.MaintenanceTypeMetricsDTO preventive = createMaintenanceTypeMetrics(
            metricsMap.get(MaintenanceType.PREVENTIVE), totalCost
        );
        
        MetricResponseDTO.MaintenanceTypeMetricsDTO corrective = createMaintenanceTypeMetrics(
            metricsMap.get(MaintenanceType.CORRECTIVE), totalCost
        );
        
        MetricResponseDTO.MaintenanceTypeMetricsDTO predictive = createMaintenanceTypeMetrics(
            metricsMap.get(MaintenanceType.PREDICTIVE), totalCost
        );
        
        return MetricResponseDTO.WorkOrderMetricsDTO.builder()
            .totalWorkOrders(totalWorkOrders)
            .completedWorkOrders(completedWorkOrders)
            .cancelledWorkOrders(cancelledWorkOrders)
            .inProgressWorkOrders(inProgressWorkOrders)
            .totalCost(totalCost)
            .averageCompletionTimeHours(BigDecimal.valueOf(averageCompletionTimeHours != null ? averageCompletionTimeHours : Double.valueOf(0.0)))
            .completionRate(completionRate)
            .preventive(preventive)
            .corrective(corrective)
            .predictive(predictive)
            .build();
    }

    public MetricResponseDTO.EquipmentMetricsDTO calculateEquipmentMetrics(MetricFilterDTO filter) {
        validateFilter(filter);
        
        Long totalEquipments = equipmentRepository.countByBuildingId(filter.getBuildingId());
        Long activeEquipments = equipmentRepository.countByStatusAndBuilding(
            filter.getBuildingId(), EquipmentStatus.ACTIVE
        );
        Long inactiveEquipments = equipmentRepository.countByStatusAndBuilding(
            filter.getBuildingId(), EquipmentStatus.INACTIVE
        );
        Long criticalEquipments = equipmentRepository.countCriticalEquipmentsByBuilding(
            filter.getBuildingId()
        );
        
        // MTBF - Mean Time Between Failures
        List<EquipmentFailureProjection> equipmentFailures = equipmentRepository.findEquipmentFailureMetrics(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        
        List<MetricResponseDTO.EquipmentMTBFDTO> equipmentMTBF = equipmentFailures.stream()
            .map(this::mapToEquipmentMTBFDTO)
            .collect(Collectors.toList());
        
        // Distribuição por status
        MetricResponseDTO.EquipmentStatusDistributionDTO statusDistribution = MetricResponseDTO.EquipmentStatusDistributionDTO.builder()
            .active(activeEquipments)
            .inactive(inactiveEquipments)
            .maintenance(equipmentRepository.countByStatusAndBuilding(filter.getBuildingId(), EquipmentStatus.UNDER_MAINTENANCE))
            .outOfService(0L) // Não existe este status no enum atual
            .build();
        
        return MetricResponseDTO.EquipmentMetricsDTO.builder()
            .totalEquipments(totalEquipments)
            .activeEquipments(activeEquipments)
            .inactiveEquipments(inactiveEquipments)
            .criticalEquipments(criticalEquipments)
            .equipmentMTBF(equipmentMTBF)
            .statusDistribution(statusDistribution)
            .build();
    }

    public MetricResponseDTO.TimeSeriesMetricsDTO calculateTimeSeriesMetrics(MetricFilterDTO filter) {
        validateFilter(filter);
        
        // Custos mensais
        List<MonthlyCostProjection> monthlyCosts = workOrderRepository.findMonthlyCostsByBuildingAndDateRange(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        
        List<MetricResponseDTO.MonthlyCostDTO> monthlyCostDTOs = monthlyCosts.stream()
            .map(this::mapToMonthlyCostDTO)
            .collect(Collectors.toList());
        
        // Ordens de serviço mensais
        List<MetricResponseDTO.MonthlyWorkOrderDTO> monthlyWorkOrders = calculateMonthlyWorkOrders(filter);
        
        // Tarefas mensais
        List<MetricResponseDTO.MonthlyTaskDTO> monthlyTasks = calculateMonthlyTasks(filter);
        
        return MetricResponseDTO.TimeSeriesMetricsDTO.builder()
            .monthlyCosts(monthlyCostDTOs)
            .monthlyWorkOrders(monthlyWorkOrders)
            .monthlyTasks(monthlyTasks)
            .build();
    }

    public MetricResponseDTO.InventoryMetricsDTO calculateInventoryMetrics(MetricFilterDTO filter) {
        validateFilter(filter);
        
        BigDecimal totalMaterialCost = inventoryRepository.sumTotalMaterialCost(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        
        List<TopUsedItemProjection> topUsedItems = inventoryRepository.findTopUsedItems(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        
        List<MetricResponseDTO.TopUsedItemDTO> topUsedItemDTOs = topUsedItems.stream()
            .map(this::mapToTopUsedItemDTO)
            .collect(Collectors.toList());
        
        List<LowStockAlertProjection> lowStockAlerts = inventoryRepository.findLowStockItems(
            filter.getBuildingId()
        );
        
        List<MetricResponseDTO.LowStockAlertDTO> lowStockAlertDTOs = lowStockAlerts.stream()
            .map(this::mapToLowStockAlertDTO)
            .collect(Collectors.toList());
        
        Long totalItemsUsed = topUsedItems.stream()
            .mapToLong(TopUsedItemProjection::getTotalQuantityUsed)
            .sum();
        
        return MetricResponseDTO.InventoryMetricsDTO.builder()
            .totalMaterialCost(totalMaterialCost)
            .totalItemsUsed(totalItemsUsed)
            .topUsedItems(topUsedItemDTOs)
            .lowStockAlerts(lowStockAlertDTOs)
            .build();
    }

    private void validateFilter(MetricFilterDTO filter) {
        if (filter.getBuildingId() == null) {
            throw new IllegalArgumentException("Building ID é obrigatório");
        }
        
        if (filter.getStartDate() != null && filter.getEndDate() != null && 
            filter.getStartDate().isAfter(filter.getEndDate())) {
            throw new IllegalArgumentException("Data inicial não pode ser maior que data final");
        }
        
        // Validar se o prédio existe
        buildingService.findById(filter.getBuildingId().toString());
    }

    private MetricResponseDTO.MaintenanceTypeMetricsDTO createMaintenanceTypeMetrics(
            MaintenanceTypeMetricProjection projection, BigDecimal totalCost) {
        
        if (projection == null) {
            return MetricResponseDTO.MaintenanceTypeMetricsDTO.builder()
                .count(0L)
                .totalCost(BigDecimal.ZERO)
                .averageCost(BigDecimal.ZERO)
                .percentageOfTotal(0.0)
                .build();
        }
        
        Double percentageOfTotal = totalCost.compareTo(BigDecimal.ZERO) > 0 ? 
            projection.getTotalCost().divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue() : 0.0;
        
        return MetricResponseDTO.MaintenanceTypeMetricsDTO.builder()
            .count(projection.getCount())
            .totalCost(projection.getTotalCost())
            .averageCost(projection.getAverageCost())
            .percentageOfTotal(percentageOfTotal)
            .build();
    }

    private MetricResponseDTO.EquipmentMTBFDTO mapToEquipmentMTBFDTO(EquipmentFailureProjection projection) {
        return MetricResponseDTO.EquipmentMTBFDTO.builder()
            .equipmentId(projection.getEquipmentId())
            .equipmentName(projection.getEquipmentName())
            .equipmentIdentification(projection.getEquipmentIdentification())
            .failureCount(projection.getFailureCount())
            .averageDaysBetweenFailures(projection.getAverageDaysBetweenFailures())
            .lastFailureDate(projection.getLastFailureDate() != null ? 
                projection.getLastFailureDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : null)
            .criticality(projection.getCriticality())
            .build();
    }

    private MetricResponseDTO.MonthlyCostDTO mapToMonthlyCostDTO(MonthlyCostProjection projection) {
        return MetricResponseDTO.MonthlyCostDTO.builder()
            .year(projection.getYear())
            .month(projection.getMonth())
            .totalCost(projection.getTotalCost())
            .workOrderCount(projection.getWorkOrderCount())
            .taskCount(projection.getTaskCount())
            .build();
    }

    private List<MetricResponseDTO.MonthlyWorkOrderDTO> calculateMonthlyWorkOrders(MetricFilterDTO filter) {
        // Implementação simplificada - em produção seria uma query mais complexa
        List<MetricResponseDTO.MonthlyWorkOrderDTO> monthlyWorkOrders = new ArrayList<>();
        
        // Aqui você implementaria a lógica para calcular ordens mensais por status
        // Por simplicidade, retornando lista vazia
        
        return monthlyWorkOrders;
    }

    private List<MetricResponseDTO.MonthlyTaskDTO> calculateMonthlyTasks(MetricFilterDTO filter) {
        List<MonthlyCostProjection> monthlyTaskCosts = taskRepository.findMonthlyTaskCostsByBuildingAndDateRange(
            filter.getBuildingId(), filter.getStartDate(), filter.getEndDate()
        );
        
        return monthlyTaskCosts.stream()
            .map(projection -> MetricResponseDTO.MonthlyTaskDTO.builder()
                .year(projection.getYear())
                .month(projection.getMonth())
                .totalCount(projection.getTaskCount())
                .completedCount(0L) // Implementar query específica se necessário
                .totalCost(projection.getTotalCost())
                .build())
            .collect(Collectors.toList());
    }

    private MetricResponseDTO.TopUsedItemDTO mapToTopUsedItemDTO(TopUsedItemProjection projection) {
        return MetricResponseDTO.TopUsedItemDTO.builder()
            .itemId(projection.getItemId())
            .itemName(projection.getItemName())
            .itemType(projection.getItemType())
            .totalQuantityUsed(projection.getTotalQuantityUsed())
            .totalCost(projection.getTotalCost())
            .currentStock(projection.getCurrentStock())
            .build();
    }

    private MetricResponseDTO.LowStockAlertDTO mapToLowStockAlertDTO(LowStockAlertProjection projection) {
        return MetricResponseDTO.LowStockAlertDTO.builder()
            .itemId(projection.getItemId())
            .itemName(projection.getItemName())
            .itemType(projection.getItemType())
            .currentQuantity(projection.getCurrentQuantity())
            .minimumQuantity(projection.getMinimumQuantity())
            .unitCost(projection.getUnitCost())
            .build();
    }
}

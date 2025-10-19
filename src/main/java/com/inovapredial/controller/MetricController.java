package com.inovapredial.controller;

import com.inovapredial.dto.MetricFilterDTO;
import com.inovapredial.dto.responses.MetricResponseDTO;
import com.inovapredial.model.enums.MaintenanceType;
import com.inovapredial.service.MetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics")
public class MetricController {

    private final MetricService metricService;
    
    /**
     * Converte string de data ISO 8601 para LocalDateTime
     * Suporta formatos: 2025-10-01T03:00:00.000Z e 2025-10-01T03:00:00
     */
    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Tenta primeiro com Z (UTC)
            if (dateTimeString.endsWith("Z")) {
                return ZonedDateTime.parse(dateTimeString).toLocalDateTime();
            }
            // Se não tem Z, tenta parse direto
            else {
                return LocalDateTime.parse(dateTimeString);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + dateTimeString + 
                ". Expected formats: 2025-10-01T03:00:00.000Z or 2025-10-01T03:00:00", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/complete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all consolidated metrics")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Metrics calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public MetricResponseDTO getCompleteMetrics(@RequestParam String buildingId,
                                               @RequestParam(required = false) String startDate,
                                               @RequestParam(required = false) String endDate,
                                               @RequestParam(required = false) String equipmentId,
                                               @RequestParam(required = false) String employeeId,
                                               @RequestParam(required = false) String maintenanceType) {
        MetricFilterDTO filter = MetricFilterDTO.builder()
            .buildingId(UUID.fromString(buildingId))
            .startDate(parseDateTime(startDate))
            .endDate(parseDateTime(endDate))
            .equipmentId(equipmentId != null ? UUID.fromString(equipmentId) : null)
            .employeeId(employeeId != null ? UUID.fromString(employeeId) : null)
            .maintenanceType(maintenanceType != null ? MaintenanceType.valueOf(maintenanceType) : null)
            .build();
        
        return metricService.calculateCompleteMetrics(filter);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/general")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get general consolidated metrics")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "General metrics calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public MetricResponseDTO.GeneralMetricsDTO getGeneralMetrics(@RequestParam String buildingId,
                                                                 @RequestParam(required = false) String startDate,
                                                                 @RequestParam(required = false) String endDate,
                                                                 @RequestParam(required = false) String equipmentId,
                                                                 @RequestParam(required = false) String employeeId,
                                                                 @RequestParam(required = false) String maintenanceType) {
        MetricFilterDTO filter = MetricFilterDTO.builder()
            .buildingId(UUID.fromString(buildingId))
            .startDate(parseDateTime(startDate))
            .endDate(parseDateTime(endDate))
            .equipmentId(equipmentId != null ? UUID.fromString(equipmentId) : null)
            .employeeId(employeeId != null ? UUID.fromString(employeeId) : null)
            .maintenanceType(maintenanceType != null ? MaintenanceType.valueOf(maintenanceType) : null)
            .build();
        
        return metricService.calculateGeneralMetrics(filter);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/work-orders")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get work order metrics")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Work order metrics calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public MetricResponseDTO.WorkOrderMetricsDTO getWorkOrderMetrics(@RequestParam String buildingId,
                                                                     @RequestParam(required = false) String startDate,
                                                                     @RequestParam(required = false) String endDate,
                                                                     @RequestParam(required = false) String equipmentId,
                                                                     @RequestParam(required = false) String employeeId,
                                                                     @RequestParam(required = false) String maintenanceType) {
        MetricFilterDTO filter = MetricFilterDTO.builder()
            .buildingId(UUID.fromString(buildingId))
            .startDate(parseDateTime(startDate))
            .endDate(parseDateTime(endDate))
            .equipmentId(equipmentId != null ? UUID.fromString(equipmentId) : null)
            .employeeId(employeeId != null ? UUID.fromString(employeeId) : null)
            .maintenanceType(maintenanceType != null ? MaintenanceType.valueOf(maintenanceType) : null)
            .build();
        
        return metricService.calculateWorkOrderMetrics(filter);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/equipment")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get equipment metrics and MTBF")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Equipment metrics calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public MetricResponseDTO.EquipmentMetricsDTO getEquipmentMetrics(@RequestParam String buildingId,
                                                                     @RequestParam(required = false) String startDate,
                                                                     @RequestParam(required = false) String endDate,
                                                                     @RequestParam(required = false) String equipmentId,
                                                                     @RequestParam(required = false) String employeeId,
                                                                     @RequestParam(required = false) String maintenanceType) {
        MetricFilterDTO filter = MetricFilterDTO.builder()
            .buildingId(UUID.fromString(buildingId))
            .startDate(parseDateTime(startDate))
            .endDate(parseDateTime(endDate))
            .equipmentId(equipmentId != null ? UUID.fromString(equipmentId) : null)
            .employeeId(employeeId != null ? UUID.fromString(employeeId) : null)
            .maintenanceType(maintenanceType != null ? MaintenanceType.valueOf(maintenanceType) : null)
            .build();
        
        return metricService.calculateEquipmentMetrics(filter);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/time-series")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get time series data for charts")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Time series data calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public MetricResponseDTO.TimeSeriesMetricsDTO getTimeSeriesMetrics(@RequestParam String buildingId,
                                                                       @RequestParam(required = false) String startDate,
                                                                       @RequestParam(required = false) String endDate,
                                                                       @RequestParam(required = false) String equipmentId,
                                                                       @RequestParam(required = false) String employeeId,
                                                                       @RequestParam(required = false) String maintenanceType) {
        MetricFilterDTO filter = MetricFilterDTO.builder()
            .buildingId(UUID.fromString(buildingId))
            .startDate(parseDateTime(startDate))
            .endDate(parseDateTime(endDate))
            .equipmentId(equipmentId != null ? UUID.fromString(equipmentId) : null)
            .employeeId(employeeId != null ? UUID.fromString(employeeId) : null)
            .maintenanceType(maintenanceType != null ? MaintenanceType.valueOf(maintenanceType) : null)
            .build();
        
        return metricService.calculateTimeSeriesMetrics(filter);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/inventory")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get inventory metrics")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inventory metrics calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public MetricResponseDTO.InventoryMetricsDTO getInventoryMetrics(@RequestParam String buildingId,
                                                                     @RequestParam(required = false) String startDate,
                                                                     @RequestParam(required = false) String endDate,
                                                                     @RequestParam(required = false) String equipmentId,
                                                                     @RequestParam(required = false) String employeeId,
                                                                     @RequestParam(required = false) String maintenanceType) {
        MetricFilterDTO filter = MetricFilterDTO.builder()
            .buildingId(UUID.fromString(buildingId))
            .startDate(parseDateTime(startDate))
            .endDate(parseDateTime(endDate))
            .equipmentId(equipmentId != null ? UUID.fromString(equipmentId) : null)
            .employeeId(employeeId != null ? UUID.fromString(employeeId) : null)
            .maintenanceType(maintenanceType != null ? MaintenanceType.valueOf(maintenanceType) : null)
            .build();
        
        return metricService.calculateInventoryMetrics(filter);
    }
}

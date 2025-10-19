package com.inovapredial.dto;

import com.inovapredial.model.enums.MaintenanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricFilterDTO {

    private UUID buildingId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private UUID equipmentId;

    private UUID employeeId;

    private MaintenanceType maintenanceType;
}

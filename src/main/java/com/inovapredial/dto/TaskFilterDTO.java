package com.inovapredial.dto;

import com.inovapredial.model.enums.ActivityStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskFilterDTO(
        String title,
        String description,
        ActivityStatus activityStatus,
        Integer estimatedTimeMin,
        Integer estimatedTimeMax,
        LocalDateTime startDateStart,
        LocalDateTime startDateEnd,
        LocalDateTime endDateStart,
        LocalDateTime endDateEnd,
        Integer timeSpentMin,
        Integer timeSpentMax,
        BigDecimal costMin,
        BigDecimal costMax,
        UUID workOrderId,
        UUID employeeId
) {
}



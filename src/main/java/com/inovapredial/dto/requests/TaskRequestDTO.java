package com.inovapredial.dto.requests;

import com.inovapredial.model.enums.ActivityStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskRequestDTO(
        @NotBlank(message = "Campo obrigatório")
        @Length(max = 255, message = "O campo deve ter no máximo {max} caracteres")
        String title,

        @Length(max = 5000, message = "O campo deve ter no máximo {max} caracteres")
        String description,

        ActivityStatus activityStatus,

        Integer estimatedTime,

        LocalDateTime startDate,

        LocalDateTime endDate,

        Integer timeSpent,

        BigDecimal cost,

        @NotNull(message = "Campo obrigatório")
        UUID workOrderId,

        UUID employeeId
) {
}



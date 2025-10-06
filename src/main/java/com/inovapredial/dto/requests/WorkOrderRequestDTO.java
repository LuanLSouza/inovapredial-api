package com.inovapredial.dto.requests;

import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.model.enums.MaintenanceType;
import com.inovapredial.model.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkOrderRequestDTO(
        @NotBlank(message = "Campo obrigatório")
        @Length(max = 255, message = "O campo deve ter no máximo {max} caracteres")
        String description,

        LocalDateTime openingDate,

        LocalDateTime closingDate,

        ActivityStatus activityStatus,

        Priority priority,

        @NotNull(message = "Campo obrigatório")
        MaintenanceType maintenanceType,

        BigDecimal totalCost,

        @NotNull(message = "Campo obrigatório")
        UUID equipmentId,

        UUID employeeId
) {
}

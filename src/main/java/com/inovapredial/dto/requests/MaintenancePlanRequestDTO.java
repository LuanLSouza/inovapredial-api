package com.inovapredial.dto.requests;

import com.inovapredial.model.enums.MaintenanceType;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record MaintenancePlanRequestDTO(
        @NotNull(message = "Campo obrigatório")
        Integer frequencyDays,

        @NotNull(message = "Campo obrigatório")
        Boolean requiresShutdown,

        @Length(max = 500, message = "O campo deve ter no máximo {max} caracteres")
        String description,

        @NotNull(message = "Campo obrigatório")
        MaintenanceType maintenanceType
) {
}

package com.inovapredial.dto.requests;

import jakarta.validation.constraints.NotNull;

public record EquipmentPlanUpdateRequestDTO(
        @NotNull(message = "Realized status is required")
        Boolean realized
) {
}


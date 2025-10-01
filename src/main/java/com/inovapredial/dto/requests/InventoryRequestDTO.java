package com.inovapredial.dto.requests;

import com.inovapredial.model.enums.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryRequestDTO(
        @NotNull(message = "Campo obrigatório")
        ItemType itemType,

        @NotBlank(message = "Campo obrigatório")
        @Length(max = 100, message = "O campo deve ter no máximo {max} caracteres")
        String name,

        @NotNull(message = "Campo obrigatório")
        @Positive(message = "O custo deve ser positivo")
        BigDecimal cost,

        @NotNull(message = "Campo obrigatório")
        @Positive(message = "A quantidade deve ser positiva")
        Integer quantity,

        @Positive(message = "O estoque mínimo deve ser positivo")
        Integer minimumStock,

        UUID employeeId
) {
}


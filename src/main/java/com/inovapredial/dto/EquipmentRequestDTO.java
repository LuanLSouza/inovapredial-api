package com.inovapredial.dto;

import com.inovapredial.model.enums.Criticality;
import com.inovapredial.model.enums.EquipmentStatus;
import com.inovapredial.model.enums.EquipmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EquipmentRequestDTO(
        @NotBlank(message = "Campo obrigatório")
        @Length(max = 100, message = "O campo deve ter no máximo {max} caracteres")
        String identification,

        @Length(max = 500, message = "O campo deve ter no máximo {max} caracteres")
        String description,

        @Length(max = 50, message = "O campo deve ter no máximo {max} caracteres")
        String serialNumber,

        @NotNull(message = "Campo obrigatório")
        EquipmentType classification,

        @Length(max = 100, message = "O campo deve ter no máximo {max} caracteres")
        String location,

        @NotNull(message = "Campo obrigatório")
        Criticality criticality,

        LocalDate purchaseDate,

        LocalDate warrantyEndDate,

        BigDecimal price,

        EquipmentStatus equipmentStatus,

        String imageUrl,

        @Length(max = 50, message = "O campo deve ter no máximo {max} caracteres")
        String group,

        @Length(max = 100, message = "O campo deve ter no máximo {max} caracteres")
        String model,

        @Length(max = 100, message = "O campo deve ter no máximo {max} caracteres")
        String costCenter,

        CalendarRequestDTO calendar
) {
}

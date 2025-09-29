package com.inovapredial.dto.requests;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record EmployeeRequestDTO(
        @NotBlank(message = "Campo obrigatório")
        @Length(max = 100, message = "O campo deve ter no máximo {max} caracteres")
        String name,

        @Length(max = 100, message = "O campo deve ter no máximo {max} caracteres")
        String specialty,

        @Length(max = 100, message = "O campo deve ter no máximo {max} caracteres")
        String contact,

        CalendarRequestDTO calendar
) {
}

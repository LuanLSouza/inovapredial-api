package com.inovapredial.dto.requests;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.time.LocalTime;
import java.util.UUID;

public record CalendarRequestDTO(
        UUID id,

        @Length(max = 500, message = "O campo deve ter no máximo {max} caracteres")
        String description,
        
        @NotNull(message = "Segunda-feira é obrigatório")
        Boolean monday,
        
        @NotNull(message = "Terça-feira é obrigatório")
        Boolean tuesday,
        
        @NotNull(message = "Quarta-feira é obrigatório")
        Boolean wednesday,
        
        @NotNull(message = "Quinta-feira é obrigatório")
        Boolean thursday,
        
        @NotNull(message = "Sexta-feira é obrigatório")
        Boolean friday,
        
        @NotNull(message = "Sábado é obrigatório")
        Boolean saturday,
        
        @NotNull(message = "Domingo é obrigatório")
        Boolean sunday,
        
        @NotNull(message = "Horário de início é obrigatório")
        LocalTime startTime,
        
        @NotNull(message = "Horário de fim é obrigatório")
        LocalTime endTime,
        
        @NotNull(message = "Indicador de pausa é obrigatório")
        Boolean hasBreak
) {
}


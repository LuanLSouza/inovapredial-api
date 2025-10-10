package com.inovapredial.mapper;

import com.inovapredial.dto.requests.CalendarRequestDTO;
import com.inovapredial.dto.responses.CalendarResponseDTO;
import com.inovapredial.model.Calendar;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CalendarMapper {

    Calendar toEntity(CalendarRequestDTO dto);

    CalendarResponseDTO toResponseDTO(Calendar calendar);

    void updateCalendarFromRequestDTO(CalendarRequestDTO dto, @MappingTarget Calendar calendar);
}

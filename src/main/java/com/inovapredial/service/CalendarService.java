package com.inovapredial.service;

import com.inovapredial.dto.requests.CalendarRequestDTO;
import com.inovapredial.dto.responses.CalendarResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.CalendarMapper;
import com.inovapredial.model.Calendar;
import com.inovapredial.repository.CalendarRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final CalendarMapper calendarMapper;

    @Transactional
    public Calendar createOrUpdateCalendar(CalendarRequestDTO dto) {
        if (dto.id() != null) {
            // Se tem ID, buscar existente ou criar novo
            return calendarRepository.findById(dto.id())
                    .map(existingCalendar -> {
                        calendarMapper.updateCalendarFromRequestDTO(dto, existingCalendar);
                        return calendarRepository.save(existingCalendar);
                    })
                    .orElseGet(() -> {
                        Calendar newCalendar = calendarMapper.toEntity(dto);
                        return calendarRepository.save(newCalendar);
                    });
        } else {
            // Se não tem ID, criar novo
            Calendar newCalendar = calendarMapper.toEntity(dto);
            return calendarRepository.save(newCalendar);
        }
    }

    public Calendar findById(UUID id) {
        return calendarRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Calendar not found"));
    }

    public List<CalendarResponseDTO> findAll() {
        return calendarRepository.findAll()
                .stream()
                .map(calendarMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void deleteById(UUID id) {
        if (!calendarRepository.existsById(id)) {
            throw new NotFoundException("Calendar not found");
        }
        calendarRepository.deleteById(id);
    }

    @Transactional
    public Calendar update(UUID id, CalendarRequestDTO dto) {
        Calendar existingCalendar = findById(id);
        calendarMapper.updateCalendarFromRequestDTO(dto, existingCalendar);
        return calendarRepository.save(existingCalendar);
    }

    public CalendarResponseDTO toResponseDTO(Calendar calendar) {
        return calendarMapper.toResponseDTO(calendar);
    }
}

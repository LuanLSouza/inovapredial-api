package com.inovapredial.controller;

import com.inovapredial.dto.requests.CalendarRequestDTO;
import com.inovapredial.dto.responses.CalendarResponseDTO;
import com.inovapredial.model.Calendar;
import com.inovapredial.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "API para gerenciar calendários")
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    @Operation(summary = "Criar novo calendário")
    public ResponseEntity<CalendarResponseDTO> create(@Valid @RequestBody CalendarRequestDTO dto) {
        Calendar calendar = calendarService.createOrUpdateCalendar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(calendarService.toResponseDTO(calendar));
    }

    @GetMapping
    @Operation(summary = "Listar todos os calendários")
    public ResponseEntity<List<CalendarResponseDTO>> findAll() {
        List<CalendarResponseDTO> calendars = calendarService.findAll();
        return ResponseEntity.ok(calendars);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar calendário por ID")
    public ResponseEntity<CalendarResponseDTO> findById(@PathVariable UUID id) {
        Calendar calendar = calendarService.findById(id);
        return ResponseEntity.ok(calendarService.toResponseDTO(calendar));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar calendário")
    public ResponseEntity<CalendarResponseDTO> update(@PathVariable UUID id, @Valid @RequestBody CalendarRequestDTO dto) {
        Calendar calendar = calendarService.update(id, dto);
        return ResponseEntity.ok(calendarService.toResponseDTO(calendar));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar calendário")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        calendarService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

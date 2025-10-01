package com.inovapredial.controller;

import com.inovapredial.dto.EmployeeFilterDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.requests.EmployeeRequestDTO;
import com.inovapredial.dto.responses.EmployeeResponseDTO;
import com.inovapredial.mapper.EmployeeMapper;
import com.inovapredial.model.Employee;
import com.inovapredial.service.EmployeeService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("employees")
@RequiredArgsConstructor
@Tag(name = "Employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new employee")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Employee created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public EmployeeResponseDTO create(@Valid @RequestBody EmployeeRequestDTO dto,
                                     @RequestParam String buildingId) {
        Employee created = employeeService.create(dto, buildingId);
        return employeeMapper.toResponseDTO(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing employee")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Employee or Building not found")
    })
    public EmployeeResponseDTO update(@PathVariable String id,
                                     @Valid @RequestBody EmployeeRequestDTO dto,
                                     @RequestParam String buildingId) {
        Employee updated = employeeService.update(id, dto, buildingId);
        return employeeMapper.toResponseDTO(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get employee by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Employee retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Employee or Building not found")
    })
    public EmployeeResponseDTO findById(@PathVariable String id, 
                                       @RequestParam String buildingId) {
        Employee employee = employeeService.findByIdAndBuilding(id, buildingId);
        return employeeMapper.toResponseDTO(employee);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete employee by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Employee or Building not found")
    })
    public void delete(@PathVariable String id, 
                      @RequestParam String buildingId) {
        employeeService.delete(id, buildingId);
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search employees with filters and pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Employees retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public PageResponseDTO<EmployeeResponseDTO> findAllWithFiltersPost(
            @RequestBody(required = false) EmployeeFilterDTO filter,
            @RequestParam String buildingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        
        if (filter == null) {
            filter = new EmployeeFilterDTO(null, null, null);
        }
        
        return employeeService.findAllWithFilters(filter, buildingId, page, size, sortBy, sortDirection);
    }
}


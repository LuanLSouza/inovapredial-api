package com.inovapredial.service;

import com.inovapredial.dto.EmployeeFilterDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.requests.EmployeeRequestDTO;
import com.inovapredial.dto.responses.EmployeeResponseDTO;
import com.inovapredial.exceptions.NotFoundException;
import com.inovapredial.mapper.EmployeeMapper;
import com.inovapredial.model.Building;
import com.inovapredial.model.Calendar;
import com.inovapredial.model.Employee;
import com.inovapredial.model.OwnUser;
import com.inovapredial.repository.EmployeeRepository;
import com.inovapredial.specification.EmployeeSpecification;
import com.inovapredial.validator.EmployeeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeMapper mapper;
    private final EmployeeRepository employeeRepository;
    private final BuildingService buildingService;
    private final CalendarService calendarService;
    private final SecurityContextService securityContextService;
    private final EmployeeValidator employeeValidator;

    @Transactional
    public Employee create(EmployeeRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        var toSave = mapper.toEntity(dto);
        toSave.setBuilding(building);

        // Se calendar foi fornecido, criar ou atualizar o calendar
        if (dto.calendar() != null) {
            Calendar calendar = calendarService.createOrUpdateCalendar(dto.calendar());
            toSave.setCalendar(calendar);
        }

        return employeeRepository.save(toSave);
    }

    @Transactional
    public Employee update(String id, EmployeeRequestDTO dto, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);
        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        var employeeToUpdate = findByIdAndBuilding(id, buildingId);

        mapper.updateEmployeeFromRequestDTO(dto, employeeToUpdate);

        // Se calendar foi fornecido, criar ou atualizar o calendar
        if (dto.calendar() != null) {
            Calendar calendar = calendarService.createOrUpdateCalendar(dto.calendar());
            employeeToUpdate.setCalendar(calendar);
        } else {
            employeeToUpdate.setCalendar(null);
        }

        return employeeRepository.save(employeeToUpdate);
    }

    public Employee findByIdAndBuilding(String id, String buildingId) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        return employeeRepository.findByIdAndBuilding(UUID.fromString(id), building)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
    }

    public void delete(String id, String buildingId) {
        var employee = findByIdAndBuilding(id, buildingId);
        employeeValidator.validateEmployeeDeletion(employee.getId());
        employeeRepository.delete(employee);
    }

    public PageResponseDTO<EmployeeResponseDTO> findAllWithFilters(EmployeeFilterDTO filter, String buildingId, int page, int size, String sortBy, String sortDirection) {
        OwnUser currentUser = securityContextService.getCurrentUser();
        Building building = buildingService.findById(buildingId);

        // Verificar se o usuário tem acesso ao building
        if (!building.getUsers().contains(currentUser)) {
            throw new NotFoundException("Building not found");
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Criar specification que inclui filtro por building
        Specification<Employee> spec = EmployeeSpecification.withFilters(filter)
                .and((root, query, criteriaBuilder) -> 
                    criteriaBuilder.equal(root.get("building"), building));

        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);
        
        List<EmployeeResponseDTO> content = employeePage.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();
        
        return PageResponseDTO.<EmployeeResponseDTO>builder()
                .content(content)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .hasNext(employeePage.hasNext())
                .hasPrevious(employeePage.hasPrevious())
                .build();
    }
}


package com.inovapredial.controller;

import com.inovapredial.dto.TaskFilterDTO;
import com.inovapredial.dto.requests.TaskRequestDTO;
import com.inovapredial.dto.responses.PageResponseDTO;
import com.inovapredial.dto.responses.TaskResponseDTO;
import com.inovapredial.mapper.TaskMapper;
import com.inovapredial.model.Task;
import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.service.TaskService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create multiple tasks at once")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tasks created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building or Work Order not found")
    })
    public List<TaskResponseDTO> createBatch(@Valid @RequestBody List<TaskRequestDTO> dtos,
                                             @RequestParam String buildingId) {
        List<Task> created = taskService.createBatch(dtos, buildingId);
        return created.stream().map(taskMapper::toResponseDTO).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing task")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Task, Building or Work Order not found")
    })
    public TaskResponseDTO update(@PathVariable String id,
                                  @Valid @RequestBody TaskRequestDTO dto,
                                  @RequestParam String buildingId) {
        Task updated = taskService.update(id, dto, buildingId);
        return taskMapper.toResponseDTO(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get task by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Task or Building not found")
    })
    public TaskResponseDTO findById(@PathVariable String id,
                                    @RequestParam String buildingId) {
        Task task = taskService.findByIdAndBuilding(id, buildingId);
        return taskMapper.toResponseDTO(task);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete task by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Task or Building not found")
    })
    public void delete(@PathVariable String id,
                       @RequestParam String buildingId) {
        taskService.delete(id, buildingId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Search tasks with filters and pagination")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Building not found")
    })
    public PageResponseDTO<TaskResponseDTO> findAllWithFiltersPost(
            @RequestBody(required = false) TaskFilterDTO filter,
            @RequestParam String buildingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        if (filter == null) {
            filter = new TaskFilterDTO(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        }

        return taskService.findAllWithFilters(filter, buildingId, page, size, sortBy, sortDirection);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update task status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Task or Building not found")
    })
    public TaskResponseDTO updateStatus(@PathVariable String id,
                                        @RequestParam ActivityStatus status,
                                        @RequestParam String buildingId,
                                        @Valid @RequestBody com.inovapredial.dto.requests.TaskStatusUpdateRequestDTO body) {
        Task updated = taskService.updateStatus(id, status, buildingId, body.reason());
        return taskMapper.toResponseDTO(updated);
    }
}



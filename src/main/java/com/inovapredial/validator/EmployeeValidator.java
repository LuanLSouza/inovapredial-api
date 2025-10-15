package com.inovapredial.validator;

import com.inovapredial.exceptions.DeletionBlockedException;
import com.inovapredial.repository.TaskRepository;
import com.inovapredial.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmployeeValidator {

    private final WorkOrderRepository workOrderRepository;
    private final TaskRepository taskRepository;

    public void validateEmployeeDeletion(UUID employeeId) {
        if (workOrderRepository.countByEmployeeId(employeeId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }

        if (taskRepository.countByEmployeeId(employeeId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }
    }
}

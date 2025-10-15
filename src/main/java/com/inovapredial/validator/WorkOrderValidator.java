package com.inovapredial.validator;

import com.inovapredial.model.Building;
import com.inovapredial.model.Equipment;
import com.inovapredial.model.Task;
import com.inovapredial.model.WorkOrder;
import com.inovapredial.model.enums.ActivityStatus;
import com.inovapredial.repository.TaskRepository;
import com.inovapredial.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkOrderValidator {

    private final WorkOrderRepository workOrderRepository;
    private final TaskRepository taskRepository;

    public void validateWorkOrderCreation(Equipment equipment, Building building) {
        validateOpenWorkOrderExists(equipment, building);
    }

    public void validateWorkOrderCompletion(WorkOrder workOrder) {
        validateHasCostOrTasks(workOrder);
    }

    private void validateOpenWorkOrderExists(Equipment equipment, Building building) {
        // Verificar se já existe uma ordem de serviço em aberto para este equipamento no mesmo prédio
        boolean hasOpenWorkOrder = workOrderRepository.existsByEquipmentAndBuildingAndActivityStatus(
                equipment, building, ActivityStatus.OPEN);

        if (hasOpenWorkOrder) {
            throw new IllegalArgumentException("Já existe uma ordem de serviço em aberto para este equipamento");
        }
    }

    private void validateHasCostOrTasks(WorkOrder workOrder) {
        // Verificar se a ordem de serviço possui custo total ou pelo menos uma tarefa
        boolean hasCost = workOrder.getTotalCost() != null && workOrder.getTotalCost().compareTo(BigDecimal.ZERO) > 0;
        boolean hasTasks = taskRepository.countByWorkOrderId(workOrder.getId()) > 0;

        if (!hasCost && !hasTasks) {
            throw new IllegalArgumentException("Não é possível concluir uma ordem de serviço sem custo ou tarefas relacionadas");
        }

        // Se possui tarefas, validar o status delas
        if (hasTasks) {
            validateTaskStatuses(workOrder.getId());
        }
    }

    private void validateTaskStatuses(UUID workOrderId) {
        // Buscar todas as tarefas relacionadas à ordem de serviço
        List<Task> tasks = taskRepository.findAllByWorkOrderId(workOrderId);
        
        // Verificar se todas as tarefas estão com status OPEN ou COMPLETED
        boolean hasInvalidStatus = tasks.stream()
                .anyMatch(task -> task.getActivityStatus() != ActivityStatus.OPEN && 
                                task.getActivityStatus() != ActivityStatus.COMPLETED);

        if (hasInvalidStatus) {
            throw new IllegalArgumentException("Não é possível concluir uma ordem de serviço com tarefas em status diferente de 'Em Aberto' ou 'Concluída'");
        }
    }
}

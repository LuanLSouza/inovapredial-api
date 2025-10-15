package com.inovapredial.validator;

import com.inovapredial.dto.requests.EquipmentRequestDTO;
import com.inovapredial.exceptions.DeletionBlockedException;
import com.inovapredial.repository.EquipmentPlanRepository;
import com.inovapredial.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EquipmentValidator {

    private final EquipmentPlanRepository equipmentPlanRepository;
    private final WorkOrderRepository workOrderRepository;

    public void validate(EquipmentRequestDTO dto) {
        validateWarrantyDate(dto.purchaseDate(), dto.warrantyEndDate());
    }

    public void validateEquipmentDeletion(UUID equipmentId) {
        if (equipmentPlanRepository.countByEquipmentId(equipmentId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }

        if (workOrderRepository.countByEquipmentId(equipmentId) > 0) {
            throw new DeletionBlockedException("Existe vinculos bloqueando esta operação");
        }
    }

    private void validateWarrantyDate(LocalDate purchaseDate, LocalDate warrantyEndDate) {
        if (purchaseDate != null && warrantyEndDate != null) {
            if (warrantyEndDate.isBefore(purchaseDate)) {
                throw new IllegalArgumentException("Data de garantia nao pode ser anterior a data de compra");
            }
        }
    }
}

package com.inovapredial.validator;

import com.inovapredial.dto.requests.EquipmentRequestDTO;
import com.inovapredial.exceptions.DeletionBlockedException;
import com.inovapredial.model.enums.Criticality;
import com.inovapredial.model.enums.EquipmentType;
import com.inovapredial.repository.EquipmentPlanRepository;
import com.inovapredial.repository.WorkOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipmentValidatorTest {

    private EquipmentValidator equipmentValidator;

    @Mock
    private EquipmentPlanRepository equipmentPlanRepository;

    @Mock
    private WorkOrderRepository workOrderRepository;

    @BeforeEach
    void setUp() {
        equipmentValidator = new EquipmentValidator(equipmentPlanRepository, workOrderRepository);
    }

    @Test
    void validate_WhenWarrantyDateIsAfterPurchaseDate_ShouldNotThrowException() {
        // Arrange
        EquipmentRequestDTO dto = new EquipmentRequestDTO(
            "EQ001",
            "Equipamento teste",
            "SN123",
            EquipmentType.MECHANICAL,
            "Localização",
            Criticality.HIGH,
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2024, 1, 1),
            BigDecimal.valueOf(1000.00),
            null,
            null,
            "Grupo A",
            "Modelo X",
            "Centro Custo",
            null
        );

        // Act & Assert
        assertDoesNotThrow(() -> equipmentValidator.validate(dto));
    }

    @Test
    void validate_WhenWarrantyDateIsBeforePurchaseDate_ShouldThrowIllegalArgumentException() {
        // Arrange
        EquipmentRequestDTO dto = new EquipmentRequestDTO(
            "EQ001",
            "Equipamento teste",
            "SN123",
            EquipmentType.MECHANICAL,
            "Localização",
            Criticality.HIGH,
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2022, 12, 31),
            BigDecimal.valueOf(1000.00),
            null,
            null,
            "Grupo A",
            "Modelo X",
            "Centro Custo",
            null
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            equipmentValidator.validate(dto)
        );
        
        assertEquals("Data de garantia nao pode ser anterior a data de compra", exception.getMessage());
    }

    @Test
    void validate_WhenWarrantyDateEqualsPurchaseDate_ShouldNotThrowException() {
        // Arrange
        EquipmentRequestDTO dto = new EquipmentRequestDTO(
            "EQ001",
            "Equipamento teste",
            "SN123",
            EquipmentType.MECHANICAL,
            "Localização",
            Criticality.HIGH,
            LocalDate.of(2023, 1, 1),
            LocalDate.of(2023, 1, 1),
            BigDecimal.valueOf(1000.00),
            null,
            null,
            "Grupo A",
            "Modelo X",
            "Centro Custo",
            null
        );

        // Act & Assert
        assertDoesNotThrow(() -> equipmentValidator.validate(dto));
    }

    @Test
    void validate_WhenPurchaseDateIsNull_ShouldNotThrowException() {
        // Arrange
        EquipmentRequestDTO dto = new EquipmentRequestDTO(
            "EQ001",
            "Equipamento teste",
            "SN123",
            EquipmentType.MECHANICAL,
            "Localização",
            Criticality.HIGH,
            null,
            LocalDate.of(2024, 1, 1),
            BigDecimal.valueOf(1000.00),
            null,
            null,
            "Grupo A",
            "Modelo X",
            "Centro Custo",
            null
        );

        // Act & Assert
        assertDoesNotThrow(() -> equipmentValidator.validate(dto));
    }

    @Test
    void validate_WhenWarrantyEndDateIsNull_ShouldNotThrowException() {
        // Arrange
        EquipmentRequestDTO dto = new EquipmentRequestDTO(
            "EQ001",
            "Equipamento teste",
            "SN123",
            EquipmentType.MECHANICAL,
            "Localização",
            Criticality.HIGH,
            LocalDate.of(2023, 1, 1),
            null,
            BigDecimal.valueOf(1000.00),
            null,
            null,
            "Grupo A",
            "Modelo X",
            "Centro Custo",
            null
        );

        // Act & Assert
        assertDoesNotThrow(() -> equipmentValidator.validate(dto));
    }

    @Test
    void validate_WhenBothDatesAreNull_ShouldNotThrowException() {
        // Arrange
        EquipmentRequestDTO dto = new EquipmentRequestDTO(
            "EQ001",
            "Equipamento teste",
            "SN123",
            EquipmentType.MECHANICAL,
            "Localização",
            Criticality.HIGH,
            null,
            null,
            BigDecimal.valueOf(1000.00),
            null,
            null,
            "Grupo A",
            "Modelo X",
            "Centro Custo",
            null
        );

        // Act & Assert
        assertDoesNotThrow(() -> equipmentValidator.validate(dto));
    }

    @Test
    void validateEquipmentDeletion_WhenNoLinksExist_ShouldNotThrowException() {
        // Arrange
        UUID equipmentId = UUID.randomUUID();
        when(equipmentPlanRepository.countByEquipmentId(equipmentId)).thenReturn(0L);
        when(workOrderRepository.countByEquipmentId(equipmentId)).thenReturn(0L);

        // Act & Assert
        assertDoesNotThrow(() -> equipmentValidator.validateEquipmentDeletion(equipmentId));
    }

    @Test
    void validateEquipmentDeletion_WhenEquipmentPlanExists_ShouldThrowEquipmentDeletionBlockedException() {
        // Arrange
        UUID equipmentId = UUID.randomUUID();
        when(equipmentPlanRepository.countByEquipmentId(equipmentId)).thenReturn(1L);

        // Act & Assert
        DeletionBlockedException exception = assertThrows(DeletionBlockedException.class, () ->
            equipmentValidator.validateEquipmentDeletion(equipmentId)
        );
        
        assertEquals("Existe vinculos bloqueando esta operação", exception.getMessage());
    }

    @Test
    void validateEquipmentDeletion_WhenWorkOrderExists_ShouldThrowEquipmentDeletionBlockedException() {
        // Arrange
        UUID equipmentId = UUID.randomUUID();
        when(equipmentPlanRepository.countByEquipmentId(equipmentId)).thenReturn(0L);
        when(workOrderRepository.countByEquipmentId(equipmentId)).thenReturn(1L);

        // Act & Assert
        DeletionBlockedException exception = assertThrows(DeletionBlockedException.class, () ->
            equipmentValidator.validateEquipmentDeletion(equipmentId)
        );
        
        assertEquals("Existe vinculos bloqueando esta operação", exception.getMessage());
    }

    @Test
    void validateEquipmentDeletion_WhenBothLinksExist_ShouldThrowEquipmentDeletionBlockedException() {
        // Arrange
        UUID equipmentId = UUID.randomUUID();
        when(equipmentPlanRepository.countByEquipmentId(equipmentId)).thenReturn(2L);
        when(workOrderRepository.countByEquipmentId(equipmentId)).thenReturn(3L);

        // Act & Assert
        DeletionBlockedException exception = assertThrows(DeletionBlockedException.class, () ->
            equipmentValidator.validateEquipmentDeletion(equipmentId)
        );
        
        assertEquals("Existe vinculos bloqueando esta operação", exception.getMessage());
    }
}

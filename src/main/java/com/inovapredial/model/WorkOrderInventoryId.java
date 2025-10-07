package com.inovapredial.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Embeddable
@Data
public class WorkOrderInventoryId {

    private UUID workOrderId;
    private UUID inventoryId;

}

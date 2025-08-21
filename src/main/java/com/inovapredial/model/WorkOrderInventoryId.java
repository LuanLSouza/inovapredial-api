package com.inovapredial.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class WorkOrderInventoryId {

    private String workOrderId;
    private String inventoryId;

}

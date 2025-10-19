package com.inovapredial.dto.projection;

import java.math.BigDecimal;

public interface TopUsedItemProjection {
    String getItemId();
    String getItemName();
    String getItemType();
    Long getTotalQuantityUsed();
    BigDecimal getTotalCost();
    Integer getCurrentStock();
}

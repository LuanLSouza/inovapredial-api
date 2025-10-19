package com.inovapredial.dto.projection;

import java.math.BigDecimal;

public interface LowStockAlertProjection {
    String getItemId();
    String getItemName();
    String getItemType();
    Integer getCurrentQuantity();
    Integer getMinimumQuantity();
    BigDecimal getUnitCost();
}

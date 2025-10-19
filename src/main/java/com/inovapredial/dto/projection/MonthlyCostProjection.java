package com.inovapredial.dto.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface MonthlyCostProjection {
    Integer getYear();
    Integer getMonth();
    BigDecimal getTotalCost();
    Long getWorkOrderCount();
    Long getTaskCount();
}

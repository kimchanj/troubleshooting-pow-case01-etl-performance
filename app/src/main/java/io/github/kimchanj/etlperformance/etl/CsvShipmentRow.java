package io.github.kimchanj.etlperformance.etl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CsvShipmentRow(
        int lineNumber,
        String shipmentExternalId,
        LocalDateTime shippedAt,
        String supplierCode,
        String warehouseCode,
        String productCode,
        int quantity,
        BigDecimal unitPrice) {

    public BigDecimal lineAmount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

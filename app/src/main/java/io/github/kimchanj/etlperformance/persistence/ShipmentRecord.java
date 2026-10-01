package io.github.kimchanj.etlperformance.persistence;

import java.time.LocalDateTime;

public class ShipmentRecord {
    private Long id;
    private final String externalId;
    private final LocalDateTime shippedAt;
    private final long supplierId;
    private final long warehouseId;

    public ShipmentRecord(String externalId, LocalDateTime shippedAt, long supplierId, long warehouseId) {
        this.externalId = externalId;
        this.shippedAt = shippedAt;
        this.supplierId = supplierId;
        this.warehouseId = warehouseId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getExternalId() { return externalId; }
    public LocalDateTime getShippedAt() { return shippedAt; }
    public long getSupplierId() { return supplierId; }
    public long getWarehouseId() { return warehouseId; }
}

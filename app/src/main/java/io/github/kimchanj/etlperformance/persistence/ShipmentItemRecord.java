package io.github.kimchanj.etlperformance.persistence;

import java.math.BigDecimal;

public class ShipmentItemRecord {
    private Long id;
    private final long shipmentId;
    private final long productId;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineAmount;

    public ShipmentItemRecord(long shipmentId, long productId, int quantity, BigDecimal unitPrice, BigDecimal lineAmount) {
        this.shipmentId = shipmentId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineAmount = lineAmount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getShipmentId() { return shipmentId; }
    public long getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getLineAmount() { return lineAmount; }
}

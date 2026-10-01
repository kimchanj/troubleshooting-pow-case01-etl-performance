package io.github.kimchanj.etlperformance.etl;

import io.github.kimchanj.etlperformance.persistence.EtlMapper;
import io.github.kimchanj.etlperformance.persistence.ReferenceData;
import io.github.kimchanj.etlperformance.persistence.ShipmentItemRecord;
import io.github.kimchanj.etlperformance.persistence.ShipmentRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShipmentTransactionService {
    private final EtlMapper mapper;

    public ShipmentTransactionService(EtlMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional
    public int importShipment(List<CsvShipmentRow> rows) {
        CsvShipmentRow first = rows.getFirst();
        validateConsistentHeader(rows, first);
        if (mapper.countShipment(first.shipmentExternalId()) > 0) {
            throw new EtlValidationException("Shipment already exists: " + first.shipmentExternalId());
        }

        ReferenceData supplier = requireActive(mapper.findSupplier(first.supplierCode()), "supplier", first.supplierCode());
        ReferenceData warehouse = requireActive(mapper.findWarehouse(first.warehouseCode()), "warehouse", first.warehouseCode());
        ShipmentRecord shipment = new ShipmentRecord(first.shipmentExternalId(), first.shippedAt(), supplier.id(), warehouse.id());
        mapper.insertShipment(shipment);

        for (CsvShipmentRow row : rows) {
            ReferenceData product = requireActive(mapper.findProduct(row.productCode()), "product", row.productCode());
            mapper.insertShipmentItem(new ShipmentItemRecord(shipment.getId(), product.id(), row.quantity(),
                    row.unitPrice(), row.lineAmount()));
        }
        return rows.size();
    }

    private static void validateConsistentHeader(List<CsvShipmentRow> rows, CsvShipmentRow first) {
        boolean inconsistent = rows.stream().anyMatch(row ->
                !row.shippedAt().equals(first.shippedAt())
                        || !row.supplierCode().equals(first.supplierCode())
                        || !row.warehouseCode().equals(first.warehouseCode()));
        if (inconsistent) {
            throw new EtlValidationException("Shipment header values conflict for: " + first.shipmentExternalId());
        }
    }

    private static ReferenceData requireActive(ReferenceData value, String type, String code) {
        if (value == null) {
            throw new EtlValidationException("Unknown " + type + " code: " + code);
        }
        if (!value.active()) {
            throw new EtlValidationException("Inactive " + type + " code: " + code);
        }
        return value;
    }
}

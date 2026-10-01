package io.github.kimchanj.etlperformance.etl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShipmentCsvReaderTests {
    @TempDir Path tempDir;
    private final ShipmentCsvReader reader = new ShipmentCsvReader();

    @Test
    void parsesUtf8CsvAndCalculatesLineAmount() throws Exception {
        Path csv = tempDir.resolve("input.csv");
        Files.writeString(csv, "shipment_external_id,shipped_at,supplier_code,warehouse_code,product_code,quantity,unit_price\n" +
                "SHIP-1,2026-01-15T09:30:00,SUP-1,WH-1,PROD-1,4,12.50\n");

        var rows = reader.read(csv);

        assertThat(rows).hasSize(1);
        assertThat(rows.getFirst().lineAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void rejectsInvalidQuantity() throws Exception {
        Path csv = tempDir.resolve("invalid.csv");
        Files.writeString(csv, "shipment_external_id,shipped_at,supplier_code,warehouse_code,product_code,quantity,unit_price\n" +
                "SHIP-1,2026-01-15T09:30:00,SUP-1,WH-1,PROD-1,0,12.50\n");

        assertThatThrownBy(() -> reader.read(csv))
                .isInstanceOf(EtlValidationException.class)
                .hasMessageContaining("quantity must be greater than zero");
    }
}

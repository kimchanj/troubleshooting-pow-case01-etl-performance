package io.github.kimchanj.etlperformance.etl;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class ShipmentImporterTests {
    @Test
    void fixtureIsGroupedAndImportedByShipment() {
        var transactionService = mock(ShipmentTransactionService.class);
        when(transactionService.importShipment(anyList())).thenAnswer(call -> call.<java.util.List<?>>getArgument(0).size());
        var importer = new ShipmentImporter(new ShipmentCsvReader(), transactionService);

        var result = importer.importFile(Path.of("fixtures", "baseline-shipments.csv"));

        assertThat(result.shipmentCount()).isEqualTo(3);
        assertThat(result.itemCount()).isEqualTo(6);
        verify(transactionService, times(3)).importShipment(anyList());
    }
}

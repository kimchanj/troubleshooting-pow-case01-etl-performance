package io.github.kimchanj.etlperformance.etl;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ShipmentImporter {
    private final ShipmentCsvReader csvReader;
    private final ShipmentTransactionService transactionService;

    public ShipmentImporter(ShipmentCsvReader csvReader, ShipmentTransactionService transactionService) {
        this.csvReader = csvReader;
        this.transactionService = transactionService;
    }

    public ImportResult importFile(Path path) {
        var groups = new LinkedHashMap<String, List<CsvShipmentRow>>();
        for (CsvShipmentRow row : csvReader.read(path)) {
            groups.computeIfAbsent(row.shipmentExternalId(), ignored -> new java.util.ArrayList<>()).add(row);
        }
        int itemCount = 0;
        for (List<CsvShipmentRow> rows : groups.values()) {
            itemCount += transactionService.importShipment(rows);
        }
        return new ImportResult(groups.size(), itemCount);
    }

    public record ImportResult(int shipmentCount, int itemCount) { }
}

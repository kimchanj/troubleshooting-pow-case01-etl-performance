package io.github.kimchanj.etlperformance.etl;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConditionalOnProperty(name = "etl.input-file", matchIfMissing = false)
public class BaselineEtlRunner implements ApplicationRunner {
    private final ShipmentImporter importer;
    private final String inputFile;

    public BaselineEtlRunner(ShipmentImporter importer, @Value("${etl.input-file}") String inputFile) {
        this.importer = importer;
        this.inputFile = inputFile;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (inputFile.isBlank()) {
            return;
        }
        var result = importer.importFile(Path.of(inputFile));
        System.out.printf("Imported %d shipments and %d shipment items.%n", result.shipmentCount(), result.itemCount());
    }
}

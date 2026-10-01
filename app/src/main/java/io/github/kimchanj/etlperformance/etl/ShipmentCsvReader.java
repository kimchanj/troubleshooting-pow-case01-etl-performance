package io.github.kimchanj.etlperformance.etl;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShipmentCsvReader {
    private static final List<String> HEADER = List.of(
            "shipment_external_id", "shipped_at", "supplier_code", "warehouse_code",
            "product_code", "quantity", "unit_price");

    public List<CsvShipmentRow> read(Path path) {
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null || !parseColumns(stripBom(headerLine)).equals(HEADER)) {
                throw new EtlValidationException("CSV header must be: " + String.join(",", HEADER));
            }

            List<CsvShipmentRow> rows = new ArrayList<>();
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.isBlank()) {
                    rows.add(toRow(parseColumns(line), lineNumber));
                }
            }
            if (rows.isEmpty()) {
                throw new EtlValidationException("CSV contains no data rows");
            }
            return rows;
        } catch (IOException e) {
            throw new EtlValidationException("Could not read CSV file: " + path, e);
        }
    }

    private CsvShipmentRow toRow(List<String> values, int lineNumber) {
        if (values.size() != HEADER.size()) {
            throw error(lineNumber, "expected 7 columns but found " + values.size());
        }
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).isBlank()) {
                throw error(lineNumber, HEADER.get(i) + " is required");
            }
        }
        try {
            int quantity = Integer.parseInt(values.get(5));
            BigDecimal unitPrice = new BigDecimal(values.get(6));
            if (quantity <= 0) {
                throw error(lineNumber, "quantity must be greater than zero");
            }
            if (unitPrice.signum() < 0 || unitPrice.scale() > 2 || unitPrice.precision() > 12) {
                throw error(lineNumber, "unit_price must be a non-negative decimal with at most 2 decimal places");
            }
            return new CsvShipmentRow(lineNumber, values.get(0), LocalDateTime.parse(values.get(1)),
                    values.get(2), values.get(3), values.get(4), quantity, unitPrice);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw error(lineNumber, "invalid date, quantity, or unit_price", e);
        }
    }

    static List<String> parseColumns(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (current == ',' && !quoted) {
                values.add(value.toString().trim());
                value.setLength(0);
            } else {
                value.append(current);
            }
        }
        if (quoted) {
            throw new EtlValidationException("CSV contains an unclosed quoted field");
        }
        values.add(value.toString().trim());
        return values;
    }

    private static String stripBom(String value) {
        return value.startsWith("\uFEFF") ? value.substring(1) : value;
    }

    private static EtlValidationException error(int line, String message) {
        return new EtlValidationException("CSV line " + line + ": " + message);
    }

    private static EtlValidationException error(int line, String message, Throwable cause) {
        return new EtlValidationException("CSV line " + line + ": " + message, cause);
    }
}

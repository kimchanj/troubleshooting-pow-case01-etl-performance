package io.github.kimchanj.etlperformance.etl;

import io.github.kimchanj.etlperformance.persistence.EtlMapper;
import io.github.kimchanj.etlperformance.persistence.ReferenceData;
import io.github.kimchanj.etlperformance.persistence.ShipmentItemRecord;
import io.github.kimchanj.etlperformance.persistence.ShipmentRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShipmentTransactionServiceTests {
    private final EtlMapper mapper = mock(EtlMapper.class);
    private final ShipmentTransactionService service = new ShipmentTransactionService(mapper);

    @Test
    void persistsOneHeaderAndAllItemsWithCalculatedAmounts() {
        when(mapper.findSupplier("SUP-1")).thenReturn(new ReferenceData(11, "SUP-1", "Y"));
        when(mapper.findWarehouse("WH-1")).thenReturn(new ReferenceData(21, "WH-1", "Y"));
        when(mapper.findProduct(any())).thenReturn(new ReferenceData(31, "PROD", "Y"));
        doAnswer(invocation -> { invocation.<ShipmentRecord>getArgument(0).setId(100L); return null; })
                .when(mapper).insertShipment(any());
        var time = LocalDateTime.parse("2026-01-15T09:30:00");
        var rows = List.of(
                new CsvShipmentRow(2, "SHIP-1", time, "SUP-1", "WH-1", "PROD-1", 4, new BigDecimal("12.50")),
                new CsvShipmentRow(3, "SHIP-1", time, "SUP-1", "WH-1", "PROD-2", 2, new BigDecimal("7.25")));

        assertThat(service.importShipment(rows)).isEqualTo(2);

        verify(mapper, times(1)).insertShipment(any());
        var item = org.mockito.ArgumentCaptor.forClass(ShipmentItemRecord.class);
        verify(mapper, times(2)).insertShipmentItem(item.capture());
        assertThat(item.getAllValues()).allMatch(value -> value.getShipmentId() == 100L);
        assertThat(item.getAllValues().getFirst().getLineAmount()).isEqualByComparingTo("50.00");
    }

    @Test
    void rejectsInactiveReferenceBeforePersistence() {
        when(mapper.findSupplier("SUP-1")).thenReturn(new ReferenceData(11, "SUP-1", "N"));
        var row = new CsvShipmentRow(2, "SHIP-1", LocalDateTime.parse("2026-01-15T09:30:00"),
                "SUP-1", "WH-1", "PROD-1", 1, BigDecimal.ONE);

        assertThatThrownBy(() -> service.importShipment(List.of(row)))
                .isInstanceOf(EtlValidationException.class)
                .hasMessageContaining("Inactive supplier");
        verify(mapper, never()).insertShipment(any());
    }
}

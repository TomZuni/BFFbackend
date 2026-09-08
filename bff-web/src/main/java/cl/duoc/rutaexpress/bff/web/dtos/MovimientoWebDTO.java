package cl.duoc.rutaexpress.bff.web.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Movimiento con todos sus campos, tal como los necesita una vista de detalle en escritorio. */
public record MovimientoWebDTO(
        LocalDate fecha,
        String tipoMovimiento,
        BigDecimal monto,
        String descripcion) {
}

package cl.duoc.rutaexpress.ms.clientes.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representacion COMPLETA de un movimiento de cuenta (todos los campos).
 */
public record MovimientoCuentaDTO(
        Long cuentaId,
        LocalDate fecha,
        String tipoMovimiento,
        BigDecimal monto,
        String descripcion) {
}

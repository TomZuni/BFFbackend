package cl.duoc.rutaexpress.ms.clientes.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representacion COMPLETA del resultado de un retiro, tal como la expone el
 * backend legacy. Cada BFF decide cuanto de esto exponer hacia su cliente.
 */
public record RetiroRealizadoDTO(
        Long cuentaId,
        BigDecimal montoRetirado,
        BigDecimal saldoAnterior,
        BigDecimal saldoActual,
        LocalDate fecha) {
}

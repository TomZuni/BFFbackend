package cl.duoc.rutaexpress.bff.cajero.dtos.core;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Respuesta COMPLETA del backend legacy. El BFF Cajero recorta esto al minimo. */
public record RetiroRealizadoCoreDTO(
        Long cuentaId,
        BigDecimal montoRetirado,
        BigDecimal saldoAnterior,
        BigDecimal saldoActual,
        LocalDate fecha) {
}

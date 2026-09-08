package cl.duoc.rutaexpress.bff.web.dtos;

import java.math.BigDecimal;

/** Agregado calculado por el BFF Web a partir del historial completo de movimientos. */
public record ResumenMovimientosDTO(
        BigDecimal totalRecargas,
        BigDecimal totalCobrosEnvio,
        BigDecimal totalAjustes,
        BigDecimal saldoNetoMovimientos,
        int cantidadMovimientos) {
}

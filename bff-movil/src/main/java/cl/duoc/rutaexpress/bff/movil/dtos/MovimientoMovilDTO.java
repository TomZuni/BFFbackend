package cl.duoc.rutaexpress.bff.movil.dtos;

import java.math.BigDecimal;

/** Movimiento reducido: sin descripcion ni cuentaId repetido, solo lo esencial. */
public record MovimientoMovilDTO(
        String fecha,
        BigDecimal monto,
        String tipo) {
}

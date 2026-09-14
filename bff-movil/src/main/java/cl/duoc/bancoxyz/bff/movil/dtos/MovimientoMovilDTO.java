package cl.duoc.bancoxyz.bff.movil.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Campos minimos: sin descripcion, para reducir el tamano del payload. */
public record MovimientoMovilDTO(LocalDate fecha, String tipoMovimiento, BigDecimal monto) {
}

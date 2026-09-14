package cl.duoc.bancoxyz.bff.cajero.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/** El recorte mas agresivo de los tres canales: solo fecha y monto. */
public record MovimientoCajeroDTO(LocalDate fecha, BigDecimal monto) {
}

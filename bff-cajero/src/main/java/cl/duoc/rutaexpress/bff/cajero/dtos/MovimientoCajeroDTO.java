package cl.duoc.rutaexpress.bff.cajero.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Movimiento basico: solo fecha y monto. Es el recorte mas agresivo de los
 * tres BFF, acorde a la pantalla reducida y el objetivo del canal (mostrar
 * el ultimo movimiento, no un detalle analitico).
 */
public record MovimientoCajeroDTO(LocalDate fecha, BigDecimal monto) {
}

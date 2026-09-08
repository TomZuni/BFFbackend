package cl.duoc.rutaexpress.bff.cajero.dtos;

import java.math.BigDecimal;

/**
 * Confirmacion minima de retiro: solo lo que un cajero necesita mostrar en
 * el comprobante (monto retirado y saldo resultante). No expone el saldo
 * anterior ni otros datos internos que el core si retorna.
 */
public record RetiroConfirmacionDTO(Long cuentaId, BigDecimal montoRetirado, BigDecimal saldoActual) {
}

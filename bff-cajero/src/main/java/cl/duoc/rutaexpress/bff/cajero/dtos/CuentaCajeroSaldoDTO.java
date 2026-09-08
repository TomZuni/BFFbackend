package cl.duoc.rutaexpress.bff.cajero.dtos;

import java.math.BigDecimal;

/**
 * Vista minima para consulta de saldo: nada de nombre, antiguedad ni tipo
 * de cuenta, solo lo indispensable para mostrar en la pantalla de un punto
 * de autoservicio.
 */
public record CuentaCajeroSaldoDTO(Long cuentaId, BigDecimal saldo) {
}

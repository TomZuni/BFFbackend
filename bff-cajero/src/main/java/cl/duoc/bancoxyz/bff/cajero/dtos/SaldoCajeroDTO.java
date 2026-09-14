package cl.duoc.bancoxyz.bff.cajero.dtos;

import java.math.BigDecimal;

/** Sin datos personales: solo lo estrictamente necesario para el cajero. */
public record SaldoCajeroDTO(String numeroTarjetaEnmascarado, BigDecimal saldoDisponible, String moneda) {
}

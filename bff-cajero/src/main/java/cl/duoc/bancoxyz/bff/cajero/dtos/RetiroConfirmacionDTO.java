package cl.duoc.bancoxyz.bff.cajero.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RetiroConfirmacionDTO(String numeroTarjetaEnmascarado, BigDecimal monto,
                                     BigDecimal saldoDisponible, LocalDate fecha) {
}

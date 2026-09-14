package cl.duoc.bancoxyz.bff.cajero.dtos.core;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RetiroRealizadoCoreDTO(Long cuentaId, BigDecimal monto, BigDecimal saldoAnterior,
                                      BigDecimal saldoActual, LocalDate fecha) {
}

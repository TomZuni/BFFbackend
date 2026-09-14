package cl.duoc.bancoxyz.ms.clientes.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RetiroRealizadoDTO(
        Long cuentaId,
        BigDecimal monto,
        BigDecimal saldoAnterior,
        BigDecimal saldoActual,
        LocalDate fecha) {
}

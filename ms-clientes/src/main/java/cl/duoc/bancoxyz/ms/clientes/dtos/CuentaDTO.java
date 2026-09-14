package cl.duoc.bancoxyz.ms.clientes.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuentaDTO(
        Long cuentaId,
        Long clienteId,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldo,
        String moneda,
        LocalDate fechaApertura) {
}

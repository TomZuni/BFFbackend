package cl.duoc.bancoxyz.bff.web.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuentaWebDTO(
        Long cuentaId,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldo,
        String moneda,
        LocalDate fechaApertura) {
}

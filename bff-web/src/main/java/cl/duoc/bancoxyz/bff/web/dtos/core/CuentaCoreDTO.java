package cl.duoc.bancoxyz.bff.web.dtos.core;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuentaCoreDTO(Long cuentaId, Long clienteId, String numeroCuenta, String tipoCuenta,
                             BigDecimal saldo, String moneda, LocalDate fechaApertura) {
}

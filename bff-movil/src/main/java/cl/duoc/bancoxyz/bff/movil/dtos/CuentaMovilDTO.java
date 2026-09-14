package cl.duoc.bancoxyz.bff.movil.dtos;

import java.math.BigDecimal;

/** Solo lo esencial: sin fecha de apertura ni moneda repetida por cuenta. */
public record CuentaMovilDTO(Long cuentaId, String numeroCuentaEnmascarado, String tipoCuenta, BigDecimal saldo) {
}

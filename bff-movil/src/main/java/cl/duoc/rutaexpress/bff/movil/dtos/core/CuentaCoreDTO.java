package cl.duoc.rutaexpress.bff.movil.dtos.core;

import java.math.BigDecimal;

public record CuentaCoreDTO(
        Long cuentaId,
        String nombre,
        BigDecimal saldo,
        Integer antiguedadMeses,
        String tipo) {
}

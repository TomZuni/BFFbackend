package cl.duoc.rutaexpress.bff.web.dtos;

import java.math.BigDecimal;

/** Vista de listado: ya incluye todos los campos relevantes para una tabla de escritorio. */
public record CuentaWebResumenDTO(
        Long cuentaId,
        String nombre,
        BigDecimal saldo,
        String tipo,
        Integer antiguedadMeses) {
}

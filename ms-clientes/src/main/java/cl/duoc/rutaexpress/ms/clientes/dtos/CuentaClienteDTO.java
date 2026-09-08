package cl.duoc.rutaexpress.ms.clientes.dtos;

import java.math.BigDecimal;

/**
 * Representacion COMPLETA de una cuenta, tal como la expone el backend
 * legacy: todos los campos, sin recortes ni transformaciones por cliente.
 */
public record CuentaClienteDTO(
        Long cuentaId,
        String nombre,
        BigDecimal saldo,
        Integer antiguedadMeses,
        String tipo) {
}

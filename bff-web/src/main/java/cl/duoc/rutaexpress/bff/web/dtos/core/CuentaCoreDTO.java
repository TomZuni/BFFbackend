package cl.duoc.rutaexpress.bff.web.dtos.core;

import java.math.BigDecimal;

/**
 * Espejo del contrato JSON expuesto por ms-clientes. Cada BFF define
 * su propia copia de estos DTOs "de entrada" porque, en la estrategia de
 * backends independientes, no comparte modulos con el backend central.
 */
public record CuentaCoreDTO(
        Long cuentaId,
        String nombre,
        BigDecimal saldo,
        Integer antiguedadMeses,
        String tipo) {
}

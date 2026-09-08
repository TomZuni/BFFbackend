package cl.duoc.rutaexpress.ms.clientes.dtos;

import java.math.BigDecimal;

/**
 * Solicitud de retiro de saldo, tal como la envia cualquier BFF que
 * necesite ejecutar esta operacion critica (hoy, el BFF Cajero).
 */
public record RetiroSolicitudDTO(BigDecimal monto) {
}

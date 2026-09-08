package cl.duoc.rutaexpress.bff.web.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vista de detalle "rica": cuenta completa + historial COMPLETO de
 * movimientos + resumen calculado. Pensada para un panel de escritorio con
 * graficos/tablas, donde el ancho de banda no es una restriccion.
 */
public record CuentaWebDetalleDTO(
        Long cuentaId,
        String nombre,
        BigDecimal saldo,
        String tipo,
        Integer antiguedadMeses,
        List<MovimientoWebDTO> movimientos,
        ResumenMovimientosDTO resumen) {
}

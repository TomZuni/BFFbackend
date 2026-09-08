package cl.duoc.rutaexpress.bff.movil.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vista de detalle liviana: solo campos esenciales + los ultimos movimientos
 * (no el historial completo), para minimizar el consumo de datos moviles.
 */
public record CuentaMovilDetalleDTO(
        Long id,
        String nombre,
        BigDecimal saldo,
        List<MovimientoMovilDTO> ultimosMovimientos) {
}

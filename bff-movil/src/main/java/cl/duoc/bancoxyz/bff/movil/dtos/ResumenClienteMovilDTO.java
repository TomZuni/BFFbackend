package cl.duoc.bancoxyz.bff.movil.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vista liviana pensada para la pantalla principal de la app: nombre
 * corto, saldo total agregado de todas las cuentas, y listados compactos
 * de cuentas y tarjetas (agregando ms-clientes + ms-tarjetas).
 */
public record ResumenClienteMovilDTO(
        Long clienteId,
        String nombreCorto,
        BigDecimal saldoTotal,
        String moneda,
        List<CuentaMovilDTO> cuentas,
        List<TarjetaMovilDTO> tarjetas) {
}

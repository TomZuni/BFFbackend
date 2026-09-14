package cl.duoc.bancoxyz.bff.web.dtos;

import java.util.List;

/**
 * Vista "rica" pensada para un dashboard de escritorio: cliente completo +
 * todas sus cuentas + todas sus tarjetas, agregando informacion de
 * ms-clientes y ms-tarjetas en una sola respuesta.
 */
public record DashboardClienteWebDTO(
        Long clienteId,
        String rut,
        String nombre,
        String correo,
        String telefono,
        List<CuentaWebDTO> cuentas,
        List<TarjetaWebDTO> tarjetas) {
}

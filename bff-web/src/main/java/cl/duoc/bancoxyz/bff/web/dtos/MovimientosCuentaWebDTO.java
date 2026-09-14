package cl.duoc.bancoxyz.bff.web.dtos;

import java.util.List;

public record MovimientosCuentaWebDTO(
        Long cuentaId,
        List<MovimientoWebDTO> movimientos,
        ResumenMovimientosDTO resumen) {
}

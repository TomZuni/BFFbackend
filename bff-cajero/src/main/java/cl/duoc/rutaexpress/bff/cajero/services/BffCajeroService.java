package cl.duoc.rutaexpress.bff.cajero.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.rutaexpress.bff.cajero.clients.MsClientesClient;
import cl.duoc.rutaexpress.bff.cajero.dtos.CuentaCajeroSaldoDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.MovimientoCajeroDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.RetiroConfirmacionDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.core.CuentaCoreDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.core.MovimientoCoreDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.core.RetiroRealizadoCoreDTO;

import lombok.RequiredArgsConstructor;

/**
 * Logica de personalizacion del BFF Cajero: llama al mismo backend central
 * que Web y Movil, pero entrega la vista MAS reducida y segura de las
 * tres, pensada para operaciones criticas y rapidas en un punto de
 * autoservicio (consulta de saldo, ultimos movimientos basicos y retiro).
 */
@Service
@RequiredArgsConstructor
public class BffCajeroService {

    private static final int MAX_ULTIMOS_MOVIMIENTOS = 3;

    private final MsClientesClient msClientesClient;

    public CuentaCajeroSaldoDTO consultarSaldo(Long cuentaId) {
        CuentaCoreDTO cuenta = msClientesClient.obtenerCuenta(cuentaId);
        return new CuentaCajeroSaldoDTO(cuenta.cuentaId(), cuenta.saldo());
    }

    public List<MovimientoCajeroDTO> consultarUltimosMovimientos(Long cuentaId) {
        // El core ya devuelve los movimientos ordenados por fecha descendente.
        List<MovimientoCoreDTO> movimientos = msClientesClient.obtenerMovimientos(cuentaId);
        return movimientos.stream()
                .limit(MAX_ULTIMOS_MOVIMIENTOS)
                .map(m -> new MovimientoCajeroDTO(m.fecha(), m.monto()))
                .toList();
    }

    public RetiroConfirmacionDTO realizarRetiro(Long cuentaId, BigDecimal monto) {
        RetiroRealizadoCoreDTO resultado = msClientesClient.realizarRetiro(cuentaId, monto);
        return new RetiroConfirmacionDTO(resultado.cuentaId(), resultado.montoRetirado(), resultado.saldoActual());
    }
}

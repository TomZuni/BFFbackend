package cl.duoc.rutaexpress.bff.movil.services;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.rutaexpress.bff.movil.clients.MsClientesClient;
import cl.duoc.rutaexpress.bff.movil.dtos.CuentaMovilDetalleDTO;
import cl.duoc.rutaexpress.bff.movil.dtos.CuentaMovilResumenDTO;
import cl.duoc.rutaexpress.bff.movil.dtos.MovimientoMovilDTO;
import cl.duoc.rutaexpress.bff.movil.dtos.core.CuentaCoreDTO;
import cl.duoc.rutaexpress.bff.movil.dtos.core.MovimientoCoreDTO;

import lombok.RequiredArgsConstructor;

/**
 * Logica de personalizacion del BFF Movil: llama al mismo backend central
 * que el BFF Web, pero recorta campos y limita el historial de movimientos
 * para reducir el consumo de ancho de banda en la app movil.
 */
@Service
@RequiredArgsConstructor
public class BffMovilService {

    private static final int MAX_ULTIMOS_MOVIMIENTOS = 3;

    private final MsClientesClient MsClientesClient;

    public List<CuentaMovilResumenDTO> listarCuentas() {
        return MsClientesClient.obtenerCuentas().stream()
                .map(c -> new CuentaMovilResumenDTO(c.cuentaId(), c.nombre(), c.saldo()))
                .toList();
    }

    public CuentaMovilDetalleDTO obtenerDetalle(Long cuentaId) {
        CuentaCoreDTO cuenta = MsClientesClient.obtenerCuenta(cuentaId);
        // El core ya devuelve los movimientos ordenados por fecha descendente.
        List<MovimientoCoreDTO> movimientos = MsClientesClient.obtenerMovimientos(cuentaId);

        List<MovimientoMovilDTO> ultimos = movimientos.stream()
                .limit(MAX_ULTIMOS_MOVIMIENTOS)
                .map(m -> new MovimientoMovilDTO(m.fecha().toString(), m.monto(), m.tipoMovimiento()))
                .toList();

        return new CuentaMovilDetalleDTO(cuenta.cuentaId(), cuenta.nombre(), cuenta.saldo(), ultimos);
    }
}

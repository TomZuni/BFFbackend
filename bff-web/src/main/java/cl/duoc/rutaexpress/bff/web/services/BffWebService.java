package cl.duoc.rutaexpress.bff.web.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.rutaexpress.bff.web.clients.MsClientesClient;
import cl.duoc.rutaexpress.bff.web.dtos.CuentaWebDetalleDTO;
import cl.duoc.rutaexpress.bff.web.dtos.CuentaWebResumenDTO;
import cl.duoc.rutaexpress.bff.web.dtos.MovimientoWebDTO;
import cl.duoc.rutaexpress.bff.web.dtos.ResumenMovimientosDTO;
import cl.duoc.rutaexpress.bff.web.dtos.core.CuentaCoreDTO;
import cl.duoc.rutaexpress.bff.web.dtos.core.MovimientoCoreDTO;

import lombok.RequiredArgsConstructor;

/**
 * Logica de personalizacion del BFF Web: llama al backend central y arma
 * respuestas "ricas" (todos los campos + agregados calculados), adecuadas
 * para interfaces complejas de escritorio.
 */
@Service
@RequiredArgsConstructor
public class BffWebService {

    private final MsClientesClient MsClientesClient;

    public List<CuentaWebResumenDTO> listarCuentas() {
        return MsClientesClient.obtenerCuentas().stream()
                .map(this::aResumen)
                .toList();
    }

    public CuentaWebDetalleDTO obtenerDetalle(Long cuentaId) {
        CuentaCoreDTO cuenta = MsClientesClient.obtenerCuenta(cuentaId);
        List<MovimientoCoreDTO> movimientos = MsClientesClient.obtenerMovimientos(cuentaId);

        List<MovimientoWebDTO> movimientosWeb = movimientos.stream()
                .map(m -> new MovimientoWebDTO(m.fecha(), m.tipoMovimiento(), m.monto(), m.descripcion()))
                .toList();

        return new CuentaWebDetalleDTO(
                cuenta.cuentaId(), cuenta.nombre(), cuenta.saldo(), cuenta.tipo(), cuenta.antiguedadMeses(),
                movimientosWeb, calcularResumen(movimientos));
    }

    private CuentaWebResumenDTO aResumen(CuentaCoreDTO cuenta) {
        return new CuentaWebResumenDTO(
                cuenta.cuentaId(), cuenta.nombre(), cuenta.saldo(), cuenta.tipo(), cuenta.antiguedadMeses());
    }

    private ResumenMovimientosDTO calcularResumen(List<MovimientoCoreDTO> movimientos) {
        BigDecimal totalRecargas = sumarPorTipo(movimientos, "RECARGA");
        BigDecimal totalCobros = sumarPorTipo(movimientos, "COBRO_ENVIO");
        BigDecimal totalAjustes = sumarPorTipo(movimientos, "AJUSTE");
        BigDecimal saldoNeto = movimientos.stream().map(MovimientoCoreDTO::monto).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResumenMovimientosDTO(totalRecargas, totalCobros, totalAjustes, saldoNeto, movimientos.size());
    }

    private BigDecimal sumarPorTipo(List<MovimientoCoreDTO> movimientos, String tipo) {
        return movimientos.stream()
                .filter(m -> tipo.equals(m.tipoMovimiento()))
                .map(MovimientoCoreDTO::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

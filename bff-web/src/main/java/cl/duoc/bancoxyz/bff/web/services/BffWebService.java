package cl.duoc.bancoxyz.bff.web.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.bancoxyz.bff.web.clients.MsClientesClient;
import cl.duoc.bancoxyz.bff.web.clients.MsTarjetasClient;
import cl.duoc.bancoxyz.bff.web.dtos.CuentaWebDTO;
import cl.duoc.bancoxyz.bff.web.dtos.DashboardClienteWebDTO;
import cl.duoc.bancoxyz.bff.web.dtos.MovimientoWebDTO;
import cl.duoc.bancoxyz.bff.web.dtos.MovimientosCuentaWebDTO;
import cl.duoc.bancoxyz.bff.web.dtos.ResumenMovimientosDTO;
import cl.duoc.bancoxyz.bff.web.dtos.TarjetaWebDTO;
import cl.duoc.bancoxyz.bff.web.dtos.core.ClienteCoreDTO;
import cl.duoc.bancoxyz.bff.web.dtos.core.CuentaCoreDTO;
import cl.duoc.bancoxyz.bff.web.dtos.core.MovimientoCoreDTO;
import cl.duoc.bancoxyz.bff.web.dtos.core.TarjetaCoreDTO;

import lombok.RequiredArgsConstructor;

/**
 * Logica de personalizacion del BFF Web: llama a los DOS backends
 * centrales (ms-clientes y ms-tarjetas) y agrega sus respuestas en una
 * vista "rica" (todos los campos + tarjetas + resumen calculado),
 * adecuada para una interfaz compleja de escritorio.
 */
@Service
@RequiredArgsConstructor
public class BffWebService {

    private final MsClientesClient msClientesClient;
    private final MsTarjetasClient msTarjetasClient;

    public DashboardClienteWebDTO obtenerDashboard(Long clienteId) {
        ClienteCoreDTO cliente = msClientesClient.obtenerCliente(clienteId);
        List<CuentaCoreDTO> cuentas = msClientesClient.obtenerCuentasDeCliente(clienteId);

        // Agregacion: por cada cuenta del cliente (servicio 1), se consultan
        // sus tarjetas en el servicio de tarjetas (servicio 2) y se combinan
        // en una sola respuesta.
        List<TarjetaWebDTO> tarjetas = cuentas.stream()
                .flatMap(cuenta -> msTarjetasClient.obtenerTarjetasDeCuenta(cuenta.cuentaId()).stream())
                .map(this::aTarjetaWeb)
                .toList();

        List<CuentaWebDTO> cuentasWeb = cuentas.stream().map(this::aCuentaWeb).toList();

        return new DashboardClienteWebDTO(
                cliente.clienteId(), cliente.rut(), cliente.nombre(), cliente.correo(), cliente.telefono(),
                cuentasWeb, tarjetas);
    }

    public MovimientosCuentaWebDTO obtenerMovimientos(Long cuentaId) {
        List<MovimientoCoreDTO> movimientos = msClientesClient.obtenerMovimientos(cuentaId);

        List<MovimientoWebDTO> movimientosWeb = movimientos.stream()
                .map(m -> new MovimientoWebDTO(m.fecha(), m.tipoMovimiento(), m.monto(), m.descripcion()))
                .toList();

        return new MovimientosCuentaWebDTO(cuentaId, movimientosWeb, calcularResumen(movimientos));
    }

    private CuentaWebDTO aCuentaWeb(CuentaCoreDTO c) {
        return new CuentaWebDTO(c.cuentaId(), c.numeroCuenta(), c.tipoCuenta(), c.saldo(), c.moneda(), c.fechaApertura());
    }

    private TarjetaWebDTO aTarjetaWeb(TarjetaCoreDTO t) {
        return new TarjetaWebDTO(t.tarjetaId(), t.cuentaId(), t.numeroTarjeta(), t.tipoTarjeta(), t.estado(), t.fechaVencimiento());
    }

    private ResumenMovimientosDTO calcularResumen(List<MovimientoCoreDTO> movimientos) {
        BigDecimal totalDepositos = sumarPorTipo(movimientos, "DEPOSITO").add(sumarPorTipo(movimientos, "ABONO_INTERES"));
        BigDecimal totalRetiros = sumarPorTipo(movimientos, "RETIRO_CAJERO");
        BigDecimal totalPagos = sumarPorTipo(movimientos, "PAGO").add(sumarPorTipo(movimientos, "TRANSFERENCIA"));
        BigDecimal saldoNeto = movimientos.stream().map(MovimientoCoreDTO::monto).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResumenMovimientosDTO(totalDepositos, totalRetiros.abs(), totalPagos.abs(), saldoNeto, movimientos.size());
    }

    private BigDecimal sumarPorTipo(List<MovimientoCoreDTO> movimientos, String tipo) {
        return movimientos.stream()
                .filter(m -> tipo.equals(m.tipoMovimiento()))
                .map(MovimientoCoreDTO::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

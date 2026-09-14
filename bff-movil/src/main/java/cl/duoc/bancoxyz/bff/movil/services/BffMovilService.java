package cl.duoc.bancoxyz.bff.movil.services;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.bancoxyz.bff.movil.clients.MsClientesClient;
import cl.duoc.bancoxyz.bff.movil.clients.MsTarjetasClient;
import cl.duoc.bancoxyz.bff.movil.dtos.CuentaMovilDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.MovimientoMovilDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.ResumenClienteMovilDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.TarjetaMovilDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.core.ClienteCoreDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.core.CuentaCoreDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.core.MovimientoCoreDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.core.TarjetaCoreDTO;

import lombok.RequiredArgsConstructor;

/**
 * Logica de personalizacion del BFF Movil: agrega informacion de
 * ms-clientes y ms-tarjetas, pero recorta agresivamente los campos para
 * minimizar el volumen de datos transferido a la app movil.
 */
@Service
@RequiredArgsConstructor
public class BffMovilService {

    private static final int MAX_MOVIMIENTOS = 5;

    private final MsClientesClient msClientesClient;
    private final MsTarjetasClient msTarjetasClient;

    public ResumenClienteMovilDTO obtenerResumen(Long clienteId) {
        ClienteCoreDTO cliente = msClientesClient.obtenerCliente(clienteId);
        List<CuentaCoreDTO> cuentas = msClientesClient.obtenerCuentasDeCliente(clienteId);

        List<CuentaMovilDTO> cuentasMovil = cuentas.stream().map(this::aCuentaMovil).toList();

        List<TarjetaMovilDTO> tarjetasMovil = cuentas.stream()
                .flatMap(c -> msTarjetasClient.obtenerTarjetasDeCuenta(c.cuentaId()).stream())
                .map(this::aTarjetaMovil)
                .toList();

        BigDecimal saldoTotal = cuentas.stream().map(CuentaCoreDTO::saldo).reduce(BigDecimal.ZERO, BigDecimal::add);
        String moneda = cuentas.isEmpty() ? "CLP" : cuentas.get(0).moneda();
        String nombreCorto = primerNombre(cliente.nombre());

        return new ResumenClienteMovilDTO(cliente.clienteId(), nombreCorto, saldoTotal, moneda, cuentasMovil, tarjetasMovil);
    }

    public List<MovimientoMovilDTO> obtenerUltimosMovimientos(Long cuentaId) {
        List<MovimientoCoreDTO> movimientos = msClientesClient.obtenerMovimientos(cuentaId);
        return movimientos.stream()
                .sorted(Comparator.comparing(MovimientoCoreDTO::fecha).reversed())
                .limit(MAX_MOVIMIENTOS)
                .map(m -> new MovimientoMovilDTO(m.fecha(), m.tipoMovimiento(), m.monto()))
                .toList();
    }

    private CuentaMovilDTO aCuentaMovil(CuentaCoreDTO c) {
        return new CuentaMovilDTO(c.cuentaId(), enmascararCuenta(c.numeroCuenta()), c.tipoCuenta(), c.saldo());
    }

    private TarjetaMovilDTO aTarjetaMovil(TarjetaCoreDTO t) {
        return new TarjetaMovilDTO(enmascararTarjeta(t.numeroTarjeta()), t.tipoTarjeta(), t.estado());
    }

    private String enmascararCuenta(String numeroCuenta) {
        if (numeroCuenta == null || numeroCuenta.length() < 4) return numeroCuenta;
        return "****" + numeroCuenta.substring(numeroCuenta.length() - 4);
    }

    private String enmascararTarjeta(String numeroTarjeta) {
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) return numeroTarjeta;
        return "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);
    }

    private String primerNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) return nombreCompleto;
        return nombreCompleto.trim().split(" ")[0];
    }
}

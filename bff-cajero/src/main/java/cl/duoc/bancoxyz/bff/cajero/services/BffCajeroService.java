package cl.duoc.bancoxyz.bff.cajero.services;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.bancoxyz.bff.cajero.clients.MsClientesClient;
import cl.duoc.bancoxyz.bff.cajero.clients.MsTarjetasClient;
import cl.duoc.bancoxyz.bff.cajero.dtos.MovimientoCajeroDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.RetiroConfirmacionDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.SaldoCajeroDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.core.CuentaCoreDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.core.MovimientoCoreDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.core.RetiroRealizadoCoreDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.core.TarjetaCoreDTO;
import cl.duoc.bancoxyz.bff.cajero.exceptions.TarjetaBloqueadaException;

import lombok.RequiredArgsConstructor;

/**
 * Logica de personalizacion del BFF Cajero: SIEMPRE opera a partir del
 * numero de tarjeta. Primero valida la tarjeta en ms-tarjetas (servicio
 * 1) y, solo si esta vigente, agrega datos de la cuenta asociada desde
 * ms-clientes (servicio 2). Es el recorte mas agresivo de los tres BFF:
 * sin datos personales, sin descripciones, solo lo indispensable para
 * operar en un cajero.
 */
@Service
@RequiredArgsConstructor
public class BffCajeroService {

    private static final int MAX_MOVIMIENTOS = 3;

    private final MsTarjetasClient msTarjetasClient;
    private final MsClientesClient msClientesClient;

    public SaldoCajeroDTO consultarSaldo(String numeroTarjeta) {
        TarjetaCoreDTO tarjeta = validarTarjetaVigente(numeroTarjeta);
        CuentaCoreDTO cuenta = msClientesClient.obtenerCuenta(tarjeta.cuentaId());
        return new SaldoCajeroDTO(enmascarar(numeroTarjeta), cuenta.saldo(), cuenta.moneda());
    }

    public List<MovimientoCajeroDTO> consultarUltimosMovimientos(String numeroTarjeta) {
        TarjetaCoreDTO tarjeta = validarTarjetaVigente(numeroTarjeta);
        List<MovimientoCoreDTO> movimientos = msClientesClient.obtenerMovimientos(tarjeta.cuentaId());
        return movimientos.stream()
                .sorted(Comparator.comparing(MovimientoCoreDTO::fecha).reversed())
                .limit(MAX_MOVIMIENTOS)
                .map(m -> new MovimientoCajeroDTO(m.fecha(), m.monto()))
                .toList();
    }

    public RetiroConfirmacionDTO realizarRetiro(String numeroTarjeta, BigDecimal monto) {
        TarjetaCoreDTO tarjeta = validarTarjetaVigente(numeroTarjeta);
        RetiroRealizadoCoreDTO retiro = msClientesClient.realizarRetiro(tarjeta.cuentaId(), monto);
        return new RetiroConfirmacionDTO(enmascarar(numeroTarjeta), retiro.monto(), retiro.saldoActual(), retiro.fecha());
    }

    private TarjetaCoreDTO validarTarjetaVigente(String numeroTarjeta) {
        TarjetaCoreDTO tarjeta = msTarjetasClient.obtenerPorNumero(numeroTarjeta);
        if (!"VIGENTE".equals(tarjeta.estado())) {
            throw new TarjetaBloqueadaException(numeroTarjeta);
        }
        return tarjeta;
    }

    private String enmascarar(String numeroTarjeta) {
        if (numeroTarjeta == null || numeroTarjeta.length() < 4) return numeroTarjeta;
        return "**** **** **** " + numeroTarjeta.substring(numeroTarjeta.length() - 4);
    }
}

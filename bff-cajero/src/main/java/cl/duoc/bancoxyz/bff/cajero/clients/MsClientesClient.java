package cl.duoc.bancoxyz.bff.cajero.clients;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import cl.duoc.bancoxyz.bff.cajero.dtos.core.CuentaCoreDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.core.MovimientoCoreDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.core.RetiroRealizadoCoreDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.core.RetiroSolicitudCoreDTO;
import cl.duoc.bancoxyz.bff.cajero.exceptions.CuentaNoEncontradaException;
import cl.duoc.bancoxyz.bff.cajero.exceptions.MontoInvalidoException;
import cl.duoc.bancoxyz.bff.cajero.exceptions.SaldoInsuficienteException;

/**
 * Encapsula las llamadas al backend de clientes/cuentas: saldo,
 * movimientos y la operacion critica de retiro.
 */
@Component
public class MsClientesClient {

    private final RestClient restClient;

    public MsClientesClient(@Qualifier("msClientesRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public CuentaCoreDTO obtenerCuenta(Long cuentaId) {
        try {
            return restClient.get().uri("/core/cuentas/{id}", cuentaId).retrieve().body(CuentaCoreDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CuentaNoEncontradaException(cuentaId);
        }
    }

    public List<MovimientoCoreDTO> obtenerMovimientos(Long cuentaId) {
        try {
            return restClient.get().uri("/core/cuentas/{id}/movimientos", cuentaId).retrieve()
                    .body(new ParameterizedTypeReference<List<MovimientoCoreDTO>>() { });
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CuentaNoEncontradaException(cuentaId);
        }
    }

    public RetiroRealizadoCoreDTO realizarRetiro(Long cuentaId, BigDecimal monto) {
        try {
            return restClient.post().uri("/core/cuentas/{id}/retiros", cuentaId)
                    .body(new RetiroSolicitudCoreDTO(monto))
                    .retrieve()
                    .body(RetiroRealizadoCoreDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CuentaNoEncontradaException(cuentaId);
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new MontoInvalidoException();
        } catch (HttpClientErrorException.UnprocessableEntity ex) {
            CuentaCoreDTO cuenta = obtenerCuenta(cuentaId);
            throw new SaldoInsuficienteException(monto, cuenta.saldo());
        }
    }
}

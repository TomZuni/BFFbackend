package cl.duoc.rutaexpress.bff.cajero.clients;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import cl.duoc.rutaexpress.bff.cajero.dtos.core.CuentaCoreDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.core.MovimientoCoreDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.core.RetiroRealizadoCoreDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.core.RetiroSolicitudCoreDTO;
import cl.duoc.rutaexpress.bff.cajero.exceptions.CuentaNoEncontradaException;
import cl.duoc.rutaexpress.bff.cajero.exceptions.SaldoInsuficienteException;

import lombok.RequiredArgsConstructor;

/**
 * Encapsula las llamadas HTTP al backend central (ms-clientes).
 */
@Component
@RequiredArgsConstructor
public class MsClientesClient {

    private final RestClient msClientesRestClient;

    public CuentaCoreDTO obtenerCuenta(Long cuentaId) {
        try {
            return msClientesRestClient.get()
                    .uri("/core/cuentas/{id}", cuentaId)
                    .retrieve()
                    .body(CuentaCoreDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CuentaNoEncontradaException(cuentaId);
        }
    }

    public List<MovimientoCoreDTO> obtenerMovimientos(Long cuentaId) {
        try {
            return msClientesRestClient.get()
                    .uri("/core/cuentas/{id}/movimientos", cuentaId)
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<List<MovimientoCoreDTO>>() {
                    });
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CuentaNoEncontradaException(cuentaId);
        }
    }

    public RetiroRealizadoCoreDTO realizarRetiro(Long cuentaId, BigDecimal monto) {
        try {
            return msClientesRestClient.post()
                    .uri("/core/cuentas/{id}/retiros", cuentaId)
                    .body(new RetiroSolicitudCoreDTO(monto))
                    .retrieve()
                    .body(RetiroRealizadoCoreDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CuentaNoEncontradaException(cuentaId);
        } catch (HttpClientErrorException.UnprocessableEntity ex) {
            throw new SaldoInsuficienteException(cuentaId);
        }
    }
}

package cl.duoc.rutaexpress.bff.web.clients;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import cl.duoc.rutaexpress.bff.web.dtos.core.CuentaCoreDTO;
import cl.duoc.rutaexpress.bff.web.dtos.core.MovimientoCoreDTO;
import cl.duoc.rutaexpress.bff.web.exceptions.CuentaNoEncontradaException;

import lombok.RequiredArgsConstructor;

/**
 * Encapsula las llamadas HTTP al backend central (ms-clientes).
 */
@Component
@RequiredArgsConstructor
public class MsClientesClient {

    private final RestClient msClientesRestClient;

    public List<CuentaCoreDTO> obtenerCuentas() {
        return msClientesRestClient.get()
                .uri("/core/cuentas")
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<CuentaCoreDTO>>() {
                });
    }

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
}

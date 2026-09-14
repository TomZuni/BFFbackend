package cl.duoc.bancoxyz.bff.movil.clients;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import cl.duoc.bancoxyz.bff.movil.dtos.core.ClienteCoreDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.core.CuentaCoreDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.core.MovimientoCoreDTO;
import cl.duoc.bancoxyz.bff.movil.exceptions.ClienteNoEncontradoException;
import cl.duoc.bancoxyz.bff.movil.exceptions.CuentaNoEncontradaException;

@Component
public class MsClientesClient {

    private final RestClient restClient;

    public MsClientesClient(@Qualifier("msClientesRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public ClienteCoreDTO obtenerCliente(Long clienteId) {
        try {
            return restClient.get().uri("/core/clientes/{id}", clienteId).retrieve().body(ClienteCoreDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ClienteNoEncontradoException(clienteId);
        }
    }

    public List<CuentaCoreDTO> obtenerCuentasDeCliente(Long clienteId) {
        try {
            return restClient.get().uri("/core/clientes/{id}/cuentas", clienteId).retrieve()
                    .body(new ParameterizedTypeReference<List<CuentaCoreDTO>>() { });
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ClienteNoEncontradoException(clienteId);
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
}

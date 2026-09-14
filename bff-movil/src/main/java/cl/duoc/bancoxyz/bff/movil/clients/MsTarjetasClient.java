package cl.duoc.bancoxyz.bff.movil.clients;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import cl.duoc.bancoxyz.bff.movil.dtos.core.TarjetaCoreDTO;

@Component
public class MsTarjetasClient {

    private final RestClient restClient;

    public MsTarjetasClient(@Qualifier("msTarjetasRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<TarjetaCoreDTO> obtenerTarjetasDeCuenta(Long cuentaId) {
        return restClient.get().uri("/core/cuentas/{id}/tarjetas", cuentaId).retrieve()
                .body(new ParameterizedTypeReference<List<TarjetaCoreDTO>>() { });
    }
}

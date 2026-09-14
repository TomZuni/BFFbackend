package cl.duoc.bancoxyz.bff.web.clients;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import cl.duoc.bancoxyz.bff.web.dtos.core.TarjetaCoreDTO;

/**
 * Encapsula las llamadas HTTP al SEGUNDO backend central: tarjetas.
 * Este BFF agrega la informacion de este servicio con la de
 * MsClientesClient para construir el dashboard del canal Web.
 */
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

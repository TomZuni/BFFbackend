package cl.duoc.bancoxyz.bff.cajero.clients;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import cl.duoc.bancoxyz.bff.cajero.dtos.core.TarjetaCoreDTO;
import cl.duoc.bancoxyz.bff.cajero.exceptions.TarjetaNoEncontradaException;

/**
 * Encapsula las llamadas al backend de tarjetas. El cajero SIEMPRE
 * comienza validando la tarjeta aqui antes de tocar la cuenta.
 */
@Component
public class MsTarjetasClient {

    private final RestClient restClient;

    public MsTarjetasClient(@Qualifier("msTarjetasRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public TarjetaCoreDTO obtenerPorNumero(String numeroTarjeta) {
        try {
            return restClient.get().uri("/core/tarjetas/{numero}", numeroTarjeta).retrieve().body(TarjetaCoreDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new TarjetaNoEncontradaException(numeroTarjeta);
        }
    }
}

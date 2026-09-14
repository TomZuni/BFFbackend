package cl.duoc.bancoxyz.bff.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Clientes HTTP hacia los DOS backends centrales que este BFF debe
 * integrar y agregar (ms-clientes y ms-tarjetas). Las URLs se externalizan
 * para poder apuntar a otros entornos sin recompilar.
 */
@Configuration
public class BackendClientsConfig {

    @Bean
    public RestClient msClientesRestClient(@Value("${ms-clientes.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient msTarjetasRestClient(@Value("${ms-tarjetas.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}

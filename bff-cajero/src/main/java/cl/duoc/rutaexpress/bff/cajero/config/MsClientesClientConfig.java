package cl.duoc.rutaexpress.bff.cajero.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP hacia el backend central (ms-clientes). La URL base se
 * externaliza para poder apuntar a otro entorno sin recompilar.
 */
@Configuration
public class MsClientesClientConfig {

    @Bean
    public RestClient msClientesRestClient(@Value("${ms-clientes.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}

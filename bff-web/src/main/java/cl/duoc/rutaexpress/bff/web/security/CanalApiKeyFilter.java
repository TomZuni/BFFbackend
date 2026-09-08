package cl.duoc.rutaexpress.bff.web.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Autenticacion y autorizacion propias del canal Web: cada BFF valida una
 * api-key distinta (una por canal), simulando que cada canal tiene su
 * propio mecanismo de acceso hacia su BFF. No reemplaza un esquema real de
 * seguridad (OAuth2/JWT), pero demuestra el punto del patron: cada BFF
 * controla el acceso de su propio cliente sin afectar a los demas canales.
 */
@Component
public class CanalApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_API_KEY = "X-Canal-Key";

    @Value("${canal.nombre}")
    private String nombreCanal;

    @Value("${canal.api-key}")
    private String apiKeyEsperada;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKeyRecibida = request.getHeader(HEADER_API_KEY);

        if (apiKeyRecibida == null || !apiKeyRecibida.equals(apiKeyEsperada)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"error\":\"Acceso no autorizado al BFF " + nombreCanal
                            + "\",\"detalle\":\"Header " + HEADER_API_KEY + " ausente o invalido\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

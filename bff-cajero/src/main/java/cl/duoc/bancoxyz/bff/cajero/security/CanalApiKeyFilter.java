package cl.duoc.bancoxyz.bff.cajero.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

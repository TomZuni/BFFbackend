package cl.duoc.rutaexpress.bff.movil.dtos;

import java.math.BigDecimal;

/** Vista de listado minima: solo lo esencial para una lista en la app movil. */
public record CuentaMovilResumenDTO(
        Long id,
        String nombre,
        BigDecimal saldo) {
}

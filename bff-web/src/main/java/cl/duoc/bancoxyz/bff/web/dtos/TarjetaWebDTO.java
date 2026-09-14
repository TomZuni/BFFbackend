package cl.duoc.bancoxyz.bff.web.dtos;

public record TarjetaWebDTO(
        Long tarjetaId,
        Long cuentaId,
        String numeroTarjeta,
        String tipoTarjeta,
        String estado,
        String fechaVencimiento) {
}

package cl.duoc.bancoxyz.ms.tarjetas.dtos;

public record TarjetaDTO(
        Long tarjetaId,
        String numeroTarjeta,
        Long cuentaId,
        String tipoTarjeta,
        String estado,
        String fechaVencimiento) {
}

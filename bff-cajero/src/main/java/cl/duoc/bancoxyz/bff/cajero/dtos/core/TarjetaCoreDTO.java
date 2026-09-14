package cl.duoc.bancoxyz.bff.cajero.dtos.core;

public record TarjetaCoreDTO(Long tarjetaId, String numeroTarjeta, Long cuentaId, String tipoTarjeta,
                              String estado, String fechaVencimiento) {
}

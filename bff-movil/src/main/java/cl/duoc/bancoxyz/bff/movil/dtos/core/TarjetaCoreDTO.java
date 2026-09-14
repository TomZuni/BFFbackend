package cl.duoc.bancoxyz.bff.movil.dtos.core;

public record TarjetaCoreDTO(Long tarjetaId, String numeroTarjeta, Long cuentaId, String tipoTarjeta,
                              String estado, String fechaVencimiento) {
}

package cl.duoc.bancoxyz.bff.web.dtos.core;

public record TarjetaCoreDTO(Long tarjetaId, String numeroTarjeta, Long cuentaId, String tipoTarjeta,
                              String estado, String fechaVencimiento) {
}

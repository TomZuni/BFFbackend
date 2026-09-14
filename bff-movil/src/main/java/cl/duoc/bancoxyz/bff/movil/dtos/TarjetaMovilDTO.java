package cl.duoc.bancoxyz.bff.movil.dtos;

/** Solo el numero enmascarado y el estado: la app no necesita mas para listar tarjetas. */
public record TarjetaMovilDTO(String numeroEnmascarado, String tipoTarjeta, String estado) {
}

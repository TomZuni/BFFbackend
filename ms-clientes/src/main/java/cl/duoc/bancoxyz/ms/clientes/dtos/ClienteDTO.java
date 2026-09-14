package cl.duoc.bancoxyz.ms.clientes.dtos;

public record ClienteDTO(
        Long clienteId,
        String rut,
        String nombre,
        String correo,
        String telefono) {
}

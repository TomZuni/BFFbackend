package cl.duoc.bancoxyz.bff.cajero.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TarjetaNoEncontradaException extends RuntimeException {
    public TarjetaNoEncontradaException(String numeroTarjeta) {
        super("No existe una tarjeta con numero " + numeroTarjeta);
    }
}

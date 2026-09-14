package cl.duoc.bancoxyz.bff.cajero.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** 403: la tarjeta existe, pero no esta en condiciones de operar. */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class TarjetaBloqueadaException extends RuntimeException {
    public TarjetaBloqueadaException(String numeroTarjeta) {
        super("La tarjeta " + numeroTarjeta + " se encuentra bloqueada y no puede operar en el cajero");
    }
}

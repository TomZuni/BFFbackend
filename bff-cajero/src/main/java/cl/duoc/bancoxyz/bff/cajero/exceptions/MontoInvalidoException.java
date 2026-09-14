package cl.duoc.bancoxyz.bff.cajero.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MontoInvalidoException extends RuntimeException {
    public MontoInvalidoException() {
        super("El monto a retirar debe ser mayor que cero");
    }
}

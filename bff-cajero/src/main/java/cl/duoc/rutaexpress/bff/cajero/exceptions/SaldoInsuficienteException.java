package cl.duoc.rutaexpress.bff.cajero.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Traduce el error tecnico del core (422 de ms-clientes) a un mensaje
 * simple y generico, apto para mostrarse en la pantalla de un cajero, sin
 * exponer detalles internos del backend legacy.
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(Long cuentaId) {
        super("Saldo insuficiente para realizar el retiro en la cuenta " + cuentaId);
    }
}

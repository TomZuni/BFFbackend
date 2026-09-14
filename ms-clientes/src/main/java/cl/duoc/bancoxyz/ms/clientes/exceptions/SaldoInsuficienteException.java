package cl.duoc.bancoxyz.ms.clientes.exceptions;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 422: la solicitud es sintacticamente valida, pero no se puede procesar
 * dado el estado actual (saldo) de la cuenta.
 */
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(Long cuentaId, BigDecimal montoSolicitado, BigDecimal saldoDisponible) {
        super("La cuenta " + cuentaId + " no tiene saldo suficiente para retirar " + montoSolicitado
                + " (saldo disponible: " + saldoDisponible + ")");
    }
}

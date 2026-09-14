package cl.duoc.bancoxyz.bff.cajero.exceptions;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(BigDecimal montoSolicitado, BigDecimal saldoDisponible) {
        super("Saldo insuficiente para retirar " + montoSolicitado + " (disponible: " + saldoDisponible + ")");
    }
}

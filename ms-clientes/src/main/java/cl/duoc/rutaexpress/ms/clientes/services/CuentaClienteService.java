package cl.duoc.rutaexpress.ms.clientes.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.rutaexpress.ms.clientes.dtos.CuentaClienteDTO;
import cl.duoc.rutaexpress.ms.clientes.dtos.MovimientoCuentaDTO;
import cl.duoc.rutaexpress.ms.clientes.dtos.RetiroRealizadoDTO;
import cl.duoc.rutaexpress.ms.clientes.entities.CuentaClienteEntity;
import cl.duoc.rutaexpress.ms.clientes.entities.MovimientoCuentaEntity;
import cl.duoc.rutaexpress.ms.clientes.exceptions.CuentaNoEncontradaException;
import cl.duoc.rutaexpress.ms.clientes.exceptions.MontoInvalidoException;
import cl.duoc.rutaexpress.ms.clientes.exceptions.SaldoInsuficienteException;
import cl.duoc.rutaexpress.ms.clientes.repositories.CuentaClienteRepository;
import cl.duoc.rutaexpress.ms.clientes.repositories.MovimientoCuentaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Contiene la logica de negocio de cuentas de cliente. El controller no debe
 * acceder a los repositories directamente, sino a traves de este service.
 */
@Service
@RequiredArgsConstructor
public class CuentaClienteService {

    private final CuentaClienteRepository cuentaClienteRepository;
    private final MovimientoCuentaRepository movimientoCuentaRepository;

    public List<CuentaClienteDTO> listarCuentas() {
        return cuentaClienteRepository.findAll().stream()
                .map(this::aDTO)
                .toList();
    }

    public CuentaClienteDTO obtenerCuenta(Long cuentaId) {
        CuentaClienteEntity cuenta = cuentaClienteRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));
        return aDTO(cuenta);
    }

    public List<MovimientoCuentaDTO> listarMovimientos(Long cuentaId) {
        if (!cuentaClienteRepository.existsById(cuentaId)) {
            throw new CuentaNoEncontradaException(cuentaId);
        }
        return movimientoCuentaRepository.findByCuentaIdOrderByFechaDesc(cuentaId).stream()
                .map(this::aDTO)
                .toList();
    }

    /**
     * Operacion critica tipica de un cajero: retira un monto del saldo
     * disponible, deja registrado el movimiento y devuelve el resultado
     * completo (el BFF que consuma esto decide que tanto exponer).
     */
    public RetiroRealizadoDTO realizarRetiro(Long cuentaId, BigDecimal monto) {
        CuentaClienteEntity cuenta = cuentaClienteRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));

        if (monto == null || monto.signum() <= 0) {
            throw new MontoInvalidoException();
        }
        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(cuentaId, monto, cuenta.getSaldo());
        }

        BigDecimal saldoAnterior = cuenta.getSaldo();
        BigDecimal saldoActual = saldoAnterior.subtract(monto);
        cuenta.setSaldo(saldoActual);
        cuentaClienteRepository.save(cuenta);

        LocalDate hoy = LocalDate.now();
        movimientoCuentaRepository.save(new MovimientoCuentaEntity(
                null, cuentaId, hoy, "RETIRO_CAJERO", monto.negate(), "Retiro en punto RutaExpress"));

        return new RetiroRealizadoDTO(cuentaId, monto, saldoAnterior, saldoActual, hoy);
    }

    private CuentaClienteDTO aDTO(CuentaClienteEntity entity) {
        return new CuentaClienteDTO(
                entity.getCuentaId(), entity.getNombre(), entity.getSaldo(),
                entity.getAntiguedadMeses(), entity.getTipo());
    }

    private MovimientoCuentaDTO aDTO(MovimientoCuentaEntity entity) {
        return new MovimientoCuentaDTO(
                entity.getCuentaId(), entity.getFecha(), entity.getTipoMovimiento(),
                entity.getMonto(), entity.getDescripcion());
    }
}

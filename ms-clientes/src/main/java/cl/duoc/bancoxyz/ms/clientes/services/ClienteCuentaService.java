package cl.duoc.bancoxyz.ms.clientes.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.bancoxyz.ms.clientes.dtos.ClienteDTO;
import cl.duoc.bancoxyz.ms.clientes.dtos.CuentaDTO;
import cl.duoc.bancoxyz.ms.clientes.dtos.MovimientoDTO;
import cl.duoc.bancoxyz.ms.clientes.dtos.RetiroRealizadoDTO;
import cl.duoc.bancoxyz.ms.clientes.entities.ClienteEntity;
import cl.duoc.bancoxyz.ms.clientes.entities.CuentaEntity;
import cl.duoc.bancoxyz.ms.clientes.entities.MovimientoEntity;
import cl.duoc.bancoxyz.ms.clientes.exceptions.ClienteNoEncontradoException;
import cl.duoc.bancoxyz.ms.clientes.exceptions.CuentaNoEncontradaException;
import cl.duoc.bancoxyz.ms.clientes.exceptions.MontoInvalidoException;
import cl.duoc.bancoxyz.ms.clientes.exceptions.SaldoInsuficienteException;
import cl.duoc.bancoxyz.ms.clientes.repositories.ClienteRepository;
import cl.duoc.bancoxyz.ms.clientes.repositories.CuentaRepository;
import cl.duoc.bancoxyz.ms.clientes.repositories.MovimientoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Logica de negocio del core bancario. Los controllers no acceden a los
 * repositories directamente, solo a traves de este service.
 */
@Service
@RequiredArgsConstructor
public class ClienteCuentaService {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    public ClienteDTO obtenerCliente(Long clienteId) {
        ClienteEntity cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException(clienteId));
        return aDTO(cliente);
    }

    public List<CuentaDTO> listarCuentasDeCliente(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNoEncontradoException(clienteId);
        }
        return cuentaRepository.findByClienteId(clienteId).stream().map(this::aDTO).toList();
    }

    public CuentaDTO obtenerCuenta(Long cuentaId) {
        CuentaEntity cuenta = cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));
        return aDTO(cuenta);
    }

    public List<MovimientoDTO> listarMovimientos(Long cuentaId) {
        if (!cuentaRepository.existsById(cuentaId)) {
            throw new CuentaNoEncontradaException(cuentaId);
        }
        return movimientoRepository.findByCuentaIdOrderByFechaDesc(cuentaId).stream().map(this::aDTO).toList();
    }

    /**
     * Operacion critica tipica de un cajero automatico: retira un monto del
     * saldo disponible, deja registrado el movimiento y devuelve el
     * resultado completo (cada BFF decide cuanto de esto exponer).
     */
    public RetiroRealizadoDTO realizarRetiro(Long cuentaId, BigDecimal monto) {
        CuentaEntity cuenta = cuentaRepository.findById(cuentaId)
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
        cuentaRepository.save(cuenta);

        LocalDate hoy = LocalDate.now();
        movimientoRepository.save(new MovimientoEntity(
                null, cuentaId, hoy, "RETIRO_CAJERO", monto.negate(), "Retiro en cajero automatico"));

        return new RetiroRealizadoDTO(cuentaId, monto, saldoAnterior, saldoActual, hoy);
    }

    private ClienteDTO aDTO(ClienteEntity e) {
        return new ClienteDTO(e.getClienteId(), e.getRut(), e.getNombre(), e.getCorreo(), e.getTelefono());
    }

    private CuentaDTO aDTO(CuentaEntity e) {
        return new CuentaDTO(e.getCuentaId(), e.getClienteId(), e.getNumeroCuenta(), e.getTipoCuenta(),
                e.getSaldo(), e.getMoneda(), e.getFechaApertura());
    }

    private MovimientoDTO aDTO(MovimientoEntity e) {
        return new MovimientoDTO(e.getCuentaId(), e.getFecha(), e.getTipoMovimiento(), e.getMonto(), e.getDescripcion());
    }
}

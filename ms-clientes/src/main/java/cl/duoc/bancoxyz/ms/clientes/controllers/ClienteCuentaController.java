package cl.duoc.bancoxyz.ms.clientes.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.bancoxyz.ms.clientes.dtos.ClienteDTO;
import cl.duoc.bancoxyz.ms.clientes.dtos.CuentaDTO;
import cl.duoc.bancoxyz.ms.clientes.dtos.MovimientoDTO;
import cl.duoc.bancoxyz.ms.clientes.dtos.RetiroRealizadoDTO;
import cl.duoc.bancoxyz.ms.clientes.dtos.RetiroSolicitudDTO;
import cl.duoc.bancoxyz.ms.clientes.services.ClienteCuentaService;

import lombok.RequiredArgsConstructor;

/**
 * API "legacy" central: expone SIEMPRE el dato completo, igual para
 * cualquier consumidor (web, movil, cajero). Es exactamente el backend
 * generalizado que el patron BFF busca dejar de exponer directamente a los
 * frontends: cada BFF de esta semana consume esta API (y la de
 * ms-tarjetas) y la adapta segun su canal.
 */
@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class ClienteCuentaController {

    private final ClienteCuentaService clienteCuentaService;

    @GetMapping("/clientes/{clienteId}")
    public ClienteDTO obtenerCliente(@PathVariable Long clienteId) {
        return clienteCuentaService.obtenerCliente(clienteId);
    }

    @GetMapping("/clientes/{clienteId}/cuentas")
    public List<CuentaDTO> listarCuentasDeCliente(@PathVariable Long clienteId) {
        return clienteCuentaService.listarCuentasDeCliente(clienteId);
    }

    @GetMapping("/cuentas/{cuentaId}")
    public CuentaDTO obtenerCuenta(@PathVariable Long cuentaId) {
        return clienteCuentaService.obtenerCuenta(cuentaId);
    }

    @GetMapping("/cuentas/{cuentaId}/movimientos")
    public List<MovimientoDTO> listarMovimientos(@PathVariable Long cuentaId) {
        return clienteCuentaService.listarMovimientos(cuentaId);
    }

    @PostMapping("/cuentas/{cuentaId}/retiros")
    public RetiroRealizadoDTO realizarRetiro(@PathVariable Long cuentaId, @RequestBody RetiroSolicitudDTO solicitud) {
        return clienteCuentaService.realizarRetiro(cuentaId, solicitud.monto());
    }
}

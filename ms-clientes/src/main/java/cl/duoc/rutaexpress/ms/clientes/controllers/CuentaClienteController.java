package cl.duoc.rutaexpress.ms.clientes.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.rutaexpress.ms.clientes.dtos.CuentaClienteDTO;
import cl.duoc.rutaexpress.ms.clientes.dtos.MovimientoCuentaDTO;
import cl.duoc.rutaexpress.ms.clientes.dtos.RetiroRealizadoDTO;
import cl.duoc.rutaexpress.ms.clientes.dtos.RetiroSolicitudDTO;
import cl.duoc.rutaexpress.ms.clientes.services.CuentaClienteService;

import lombok.RequiredArgsConstructor;

/**
 * API "legacy" central: expone SIEMPRE el dato completo, igual para
 * cualquier consumidor (web, movil, cajero). Es exactamente el backend
 * generalizado que el patron BFF busca dejar de exponer directamente a los
 * frontends: cada BFF de esta semana consume esta API y la adapta.
 */
@RestController
@RequestMapping("/core/cuentas")
@RequiredArgsConstructor
public class CuentaClienteController {

    private final CuentaClienteService cuentaClienteService;

    @GetMapping
    public List<CuentaClienteDTO> listarCuentas() {
        return cuentaClienteService.listarCuentas();
    }

    @GetMapping("/{cuentaId}")
    public CuentaClienteDTO obtenerCuenta(@PathVariable Long cuentaId) {
        return cuentaClienteService.obtenerCuenta(cuentaId);
    }

    @GetMapping("/{cuentaId}/movimientos")
    public List<MovimientoCuentaDTO> listarMovimientos(@PathVariable Long cuentaId) {
        return cuentaClienteService.listarMovimientos(cuentaId);
    }

    @PostMapping("/{cuentaId}/retiros")
    public RetiroRealizadoDTO realizarRetiro(@PathVariable Long cuentaId, @RequestBody RetiroSolicitudDTO solicitud) {
        return cuentaClienteService.realizarRetiro(cuentaId, solicitud.monto());
    }
}

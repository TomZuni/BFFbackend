package cl.duoc.bancoxyz.bff.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.bancoxyz.bff.web.dtos.DashboardClienteWebDTO;
import cl.duoc.bancoxyz.bff.web.dtos.MovimientosCuentaWebDTO;
import cl.duoc.bancoxyz.bff.web.services.BffWebService;

import lombok.RequiredArgsConstructor;

/**
 * BFF Web: expone datos completos y agregados (cliente + cuentas +
 * tarjetas + movimientos + resumen), optimizado para una interfaz de
 * escritorio compleja.
 */
@RestController
@RequestMapping("/bff/web")
@RequiredArgsConstructor
public class DashboardWebController {

    private final BffWebService bffWebService;

    @GetMapping("/clientes/{clienteId}/dashboard")
    public DashboardClienteWebDTO obtenerDashboard(@PathVariable Long clienteId) {
        return bffWebService.obtenerDashboard(clienteId);
    }

    @GetMapping("/cuentas/{cuentaId}/movimientos")
    public MovimientosCuentaWebDTO obtenerMovimientos(@PathVariable Long cuentaId) {
        return bffWebService.obtenerMovimientos(cuentaId);
    }
}

package cl.duoc.rutaexpress.bff.web.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.rutaexpress.bff.web.dtos.CuentaWebDetalleDTO;
import cl.duoc.rutaexpress.bff.web.dtos.CuentaWebResumenDTO;
import cl.duoc.rutaexpress.bff.web.services.BffWebService;

import lombok.RequiredArgsConstructor;

/**
 * BFF Web: expone datos completos, optimizados para una interfaz de
 * escritorio compleja (tablas, graficos, resumenes).
 */
@RestController
@RequestMapping("/bff/web/cuentas")
@RequiredArgsConstructor
public class CuentaWebController {

    private final BffWebService bffWebService;

    @GetMapping
    public List<CuentaWebResumenDTO> listarCuentas() {
        return bffWebService.listarCuentas();
    }

    @GetMapping("/{cuentaId}")
    public CuentaWebDetalleDTO obtenerDetalle(@PathVariable Long cuentaId) {
        return bffWebService.obtenerDetalle(cuentaId);
    }
}

package cl.duoc.rutaexpress.bff.movil.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.rutaexpress.bff.movil.dtos.CuentaMovilDetalleDTO;
import cl.duoc.rutaexpress.bff.movil.dtos.CuentaMovilResumenDTO;
import cl.duoc.rutaexpress.bff.movil.services.BffMovilService;

import lombok.RequiredArgsConstructor;

/**
 * BFF Movil: expone solo los datos esenciales, con respuestas livianas
 * pensadas para reducir el consumo de ancho de banda de la app movil.
 */
@RestController
@RequestMapping("/bff/movil/cuentas")
@RequiredArgsConstructor
public class CuentaMovilController {

    private final BffMovilService bffMovilService;

    @GetMapping
    public List<CuentaMovilResumenDTO> listarCuentas() {
        return bffMovilService.listarCuentas();
    }

    @GetMapping("/{cuentaId}")
    public CuentaMovilDetalleDTO obtenerDetalle(@PathVariable Long cuentaId) {
        return bffMovilService.obtenerDetalle(cuentaId);
    }
}

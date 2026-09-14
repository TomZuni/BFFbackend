package cl.duoc.bancoxyz.bff.movil.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.bancoxyz.bff.movil.dtos.MovimientoMovilDTO;
import cl.duoc.bancoxyz.bff.movil.dtos.ResumenClienteMovilDTO;
import cl.duoc.bancoxyz.bff.movil.services.BffMovilService;

import lombok.RequiredArgsConstructor;

/**
 * BFF Movil: respuestas ligeras (campos esenciales + tarjetas y numeros
 * enmascarados + solo los ultimos movimientos) para minimizar el consumo
 * de datos y mejorar la velocidad de la app.
 */
@RestController
@RequestMapping("/bff/movil")
@RequiredArgsConstructor
public class ResumenMovilController {

    private final BffMovilService bffMovilService;

    @GetMapping("/clientes/{clienteId}/resumen")
    public ResumenClienteMovilDTO obtenerResumen(@PathVariable Long clienteId) {
        return bffMovilService.obtenerResumen(clienteId);
    }

    @GetMapping("/cuentas/{cuentaId}/movimientos")
    public List<MovimientoMovilDTO> obtenerUltimosMovimientos(@PathVariable Long cuentaId) {
        return bffMovilService.obtenerUltimosMovimientos(cuentaId);
    }
}

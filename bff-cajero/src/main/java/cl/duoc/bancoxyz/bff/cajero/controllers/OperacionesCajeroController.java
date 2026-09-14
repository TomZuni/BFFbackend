package cl.duoc.bancoxyz.bff.cajero.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.bancoxyz.bff.cajero.dtos.MovimientoCajeroDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.RetiroConfirmacionDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.RetiroSolicitudDTO;
import cl.duoc.bancoxyz.bff.cajero.dtos.SaldoCajeroDTO;
import cl.duoc.bancoxyz.bff.cajero.services.BffCajeroService;

import lombok.RequiredArgsConstructor;

/**
 * BFF Cajero: interfaz segura y minima para operaciones criticas
 * (consulta de saldo, ultimos movimientos y retiro), identificando al
 * cliente SIEMPRE por su numero de tarjeta, sin exponer datos personales.
 */
@RestController
@RequestMapping("/bff/cajero/tarjetas")
@RequiredArgsConstructor
public class OperacionesCajeroController {

    private final BffCajeroService bffCajeroService;

    @GetMapping("/{numeroTarjeta}/saldo")
    public SaldoCajeroDTO consultarSaldo(@PathVariable String numeroTarjeta) {
        return bffCajeroService.consultarSaldo(numeroTarjeta);
    }

    @GetMapping("/{numeroTarjeta}/movimientos")
    public List<MovimientoCajeroDTO> consultarUltimosMovimientos(@PathVariable String numeroTarjeta) {
        return bffCajeroService.consultarUltimosMovimientos(numeroTarjeta);
    }

    @PostMapping("/{numeroTarjeta}/retiros")
    public RetiroConfirmacionDTO realizarRetiro(@PathVariable String numeroTarjeta, @RequestBody RetiroSolicitudDTO solicitud) {
        return bffCajeroService.realizarRetiro(numeroTarjeta, solicitud.monto());
    }
}

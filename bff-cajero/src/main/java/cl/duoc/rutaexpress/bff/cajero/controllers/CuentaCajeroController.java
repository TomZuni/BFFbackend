package cl.duoc.rutaexpress.bff.cajero.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.rutaexpress.bff.cajero.dtos.CuentaCajeroSaldoDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.MovimientoCajeroDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.RetiroConfirmacionDTO;
import cl.duoc.rutaexpress.bff.cajero.dtos.RetiroSolicitudDTO;
import cl.duoc.rutaexpress.bff.cajero.services.BffCajeroService;

import lombok.RequiredArgsConstructor;

/**
 * BFF Cajero: interfaz minima y seguro para operaciones criticas
 * (consulta de saldo, ultimos movimientos basicos y retiro), sin exponer
 * datos ni endpoints que este canal no necesita.
 */
@RestController
@RequestMapping("/bff/cajero/cuentas")
@RequiredArgsConstructor
public class CuentaCajeroController {

    private final BffCajeroService bffCajeroService;

    @GetMapping("/{cuentaId}/saldo")
    public CuentaCajeroSaldoDTO consultarSaldo(@PathVariable Long cuentaId) {
        return bffCajeroService.consultarSaldo(cuentaId);
    }

    @GetMapping("/{cuentaId}/movimientos")
    public List<MovimientoCajeroDTO> consultarUltimosMovimientos(@PathVariable Long cuentaId) {
        return bffCajeroService.consultarUltimosMovimientos(cuentaId);
    }

    @PostMapping("/{cuentaId}/retiros")
    public RetiroConfirmacionDTO realizarRetiro(@PathVariable Long cuentaId, @RequestBody RetiroSolicitudDTO solicitud) {
        return bffCajeroService.realizarRetiro(cuentaId, solicitud.monto());
    }
}

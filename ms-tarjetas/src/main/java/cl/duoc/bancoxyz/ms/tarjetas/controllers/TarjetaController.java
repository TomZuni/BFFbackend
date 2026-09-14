package cl.duoc.bancoxyz.ms.tarjetas.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.bancoxyz.ms.tarjetas.dtos.TarjetaDTO;
import cl.duoc.bancoxyz.ms.tarjetas.services.TarjetaService;

import lombok.RequiredArgsConstructor;

/**
 * Segundo backend "legacy": expone SIEMPRE el dato completo de tarjetas,
 * sin adaptacion por canal. Los BFF lo consumen junto con ms-clientes para
 * construir respuestas agregadas.
 */
@RestController
@RequestMapping("/core")
@RequiredArgsConstructor
public class TarjetaController {

    private final TarjetaService tarjetaService;

    @GetMapping("/tarjetas/{numeroTarjeta}")
    public TarjetaDTO obtenerPorNumero(@PathVariable String numeroTarjeta) {
        return tarjetaService.obtenerPorNumero(numeroTarjeta);
    }

    @GetMapping("/cuentas/{cuentaId}/tarjetas")
    public List<TarjetaDTO> listarPorCuenta(@PathVariable Long cuentaId) {
        return tarjetaService.listarPorCuenta(cuentaId);
    }
}

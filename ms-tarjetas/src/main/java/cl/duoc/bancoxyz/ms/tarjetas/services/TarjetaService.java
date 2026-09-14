package cl.duoc.bancoxyz.ms.tarjetas.services;

import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.bancoxyz.ms.tarjetas.dtos.TarjetaDTO;
import cl.duoc.bancoxyz.ms.tarjetas.entities.TarjetaEntity;
import cl.duoc.bancoxyz.ms.tarjetas.exceptions.TarjetaNoEncontradaException;
import cl.duoc.bancoxyz.ms.tarjetas.repositories.TarjetaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarjetaService {

    private final TarjetaRepository tarjetaRepository;

    public TarjetaDTO obtenerPorNumero(String numeroTarjeta) {
        TarjetaEntity tarjeta = tarjetaRepository.findByNumeroTarjeta(numeroTarjeta)
                .orElseThrow(() -> new TarjetaNoEncontradaException(numeroTarjeta));
        return aDTO(tarjeta);
    }

    public List<TarjetaDTO> listarPorCuenta(Long cuentaId) {
        return tarjetaRepository.findByCuentaId(cuentaId).stream().map(this::aDTO).toList();
    }

    private TarjetaDTO aDTO(TarjetaEntity e) {
        return new TarjetaDTO(e.getTarjetaId(), e.getNumeroTarjeta(), e.getCuentaId(), e.getTipoTarjeta(),
                e.getEstado(), e.getFechaVencimiento());
    }
}

package cl.duoc.bancoxyz.ms.tarjetas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.bancoxyz.ms.tarjetas.entities.TarjetaEntity;

public interface TarjetaRepository extends JpaRepository<TarjetaEntity, Long> {

    Optional<TarjetaEntity> findByNumeroTarjeta(String numeroTarjeta);

    List<TarjetaEntity> findByCuentaId(Long cuentaId);
}

package cl.duoc.bancoxyz.ms.clientes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.bancoxyz.ms.clientes.entities.MovimientoEntity;

public interface MovimientoRepository extends JpaRepository<MovimientoEntity, Long> {

    List<MovimientoEntity> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}

package cl.duoc.rutaexpress.ms.clientes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.rutaexpress.ms.clientes.entities.MovimientoCuentaEntity;

public interface MovimientoCuentaRepository extends JpaRepository<MovimientoCuentaEntity, Long> {

    List<MovimientoCuentaEntity> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}

package cl.duoc.bancoxyz.ms.clientes.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.bancoxyz.ms.clientes.entities.CuentaEntity;

public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {

    List<CuentaEntity> findByClienteId(Long clienteId);
}

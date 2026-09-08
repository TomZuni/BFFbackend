package cl.duoc.rutaexpress.ms.clientes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.rutaexpress.ms.clientes.entities.CuentaClienteEntity;

public interface CuentaClienteRepository extends JpaRepository<CuentaClienteEntity, Long> {
}

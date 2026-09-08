package cl.duoc.rutaexpress.ms.clientes.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuenta_cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaClienteEntity {

    @Id
    private Long cuentaId;

    private String nombre;
    private BigDecimal saldo;
    private Integer antiguedadMeses;
    private String tipo;
}

package cl.duoc.rutaexpress.ms.clientes.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movimiento_cuenta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long cuentaId;
    private LocalDate fecha;
    private String tipoMovimiento;
    private BigDecimal monto;
    private String descripcion;
}

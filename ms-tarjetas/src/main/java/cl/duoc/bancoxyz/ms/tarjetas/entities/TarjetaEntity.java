package cl.duoc.bancoxyz.ms.tarjetas.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tarjeta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaEntity {

    @Id
    private Long tarjetaId;

    private String numeroTarjeta;
    private Long cuentaId;
    private String tipoTarjeta;
    private String estado;
    private String fechaVencimiento;
}

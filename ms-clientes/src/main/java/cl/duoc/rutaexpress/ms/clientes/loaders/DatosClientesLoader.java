package cl.duoc.rutaexpress.ms.clientes.loaders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import cl.duoc.rutaexpress.ms.clientes.entities.CuentaClienteEntity;
import cl.duoc.rutaexpress.ms.clientes.entities.MovimientoCuentaEntity;
import cl.duoc.rutaexpress.ms.clientes.repositories.CuentaClienteRepository;
import cl.duoc.rutaexpress.ms.clientes.repositories.MovimientoCuentaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Carga inicial de datos "legacy" (CSV) del backend central: simula un
 * sistema existente que ya tiene los datos de cuentas y movimientos, sobre
 * el cual se construyen los BFF de esta semana.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatosClientesLoader implements CommandLineRunner {

    private final CuentaClienteRepository cuentaClienteRepository;
    private final MovimientoCuentaRepository movimientoCuentaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (cuentaClienteRepository.count() > 0) {
            return;
        }

        List<CuentaClienteEntity> cuentas = leerCuentas();
        cuentaClienteRepository.saveAll(cuentas);

        List<MovimientoCuentaEntity> movimientos = leerMovimientos();
        movimientoCuentaRepository.saveAll(movimientos);

        log.info(">> Datos legacy cargados: {} cuentas, {} movimientos", cuentas.size(), movimientos.size());
    }

    private List<CuentaClienteEntity> leerCuentas() throws Exception {
        List<CuentaClienteEntity> resultado = new ArrayList<>();
        try (BufferedReader reader = abrirLector("data/cuentas_clientes.csv")) {
            reader.readLine(); // encabezado
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                String[] campos = linea.split(",", -1);
                resultado.add(new CuentaClienteEntity(
                        Long.parseLong(campos[0].trim()),
                        campos[1].trim(),
                        new BigDecimal(campos[2].trim()),
                        Integer.parseInt(campos[3].trim()),
                        campos[4].trim()));
            }
        }
        return resultado;
    }

    private List<MovimientoCuentaEntity> leerMovimientos() throws Exception {
        List<MovimientoCuentaEntity> resultado = new ArrayList<>();
        try (BufferedReader reader = abrirLector("data/movimientos_cuenta.csv")) {
            reader.readLine(); // encabezado
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                String[] campos = linea.split(",", -1);
                resultado.add(new MovimientoCuentaEntity(
                        null,
                        Long.parseLong(campos[0].trim()),
                        LocalDate.parse(campos[1].trim()),
                        campos[2].trim(),
                        new BigDecimal(campos[3].trim()),
                        campos[4].trim()));
            }
        }
        return resultado;
    }

    private BufferedReader abrirLector(String classpathFile) throws Exception {
        InputStream input = new ClassPathResource(classpathFile).getInputStream();
        return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    }
}

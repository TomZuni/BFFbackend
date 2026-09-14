package cl.duoc.bancoxyz.ms.clientes.loaders;

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

import cl.duoc.bancoxyz.ms.clientes.entities.ClienteEntity;
import cl.duoc.bancoxyz.ms.clientes.entities.CuentaEntity;
import cl.duoc.bancoxyz.ms.clientes.entities.MovimientoEntity;
import cl.duoc.bancoxyz.ms.clientes.repositories.ClienteRepository;
import cl.duoc.bancoxyz.ms.clientes.repositories.CuentaRepository;
import cl.duoc.bancoxyz.ms.clientes.repositories.MovimientoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Carga inicial de datos "legacy" (CSV) del core bancario: simula un
 * sistema existente (inspirado en la estructura de datos de
 * https://github.com/KariVillagran/bank_legacy_data) sobre el cual se
 * construyen los BFF de esta semana. Los archivos de
 * src/main/resources/data pueden reemplazarse por los datos oficiales del
 * repositorio sin tocar el resto del codigo, siempre que se respete el
 * formato de columnas.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatosLegacyLoader implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (clienteRepository.count() > 0) {
            return;
        }

        List<ClienteEntity> clientes = leerClientes();
        clienteRepository.saveAll(clientes);

        List<CuentaEntity> cuentas = leerCuentas();
        cuentaRepository.saveAll(cuentas);

        List<MovimientoEntity> movimientos = leerMovimientos();
        movimientoRepository.saveAll(movimientos);

        log.info(">> Datos legacy cargados: {} clientes, {} cuentas, {} movimientos",
                clientes.size(), cuentas.size(), movimientos.size());
    }

    private List<ClienteEntity> leerClientes() throws Exception {
        List<ClienteEntity> resultado = new ArrayList<>();
        try (BufferedReader reader = abrirLector("data/clientes.csv")) {
            reader.readLine();
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] c = linea.split(",", -1);
                resultado.add(new ClienteEntity(
                        Long.parseLong(c[0].trim()), c[1].trim(), c[2].trim(), c[3].trim(), c[4].trim()));
            }
        }
        return resultado;
    }

    private List<CuentaEntity> leerCuentas() throws Exception {
        List<CuentaEntity> resultado = new ArrayList<>();
        try (BufferedReader reader = abrirLector("data/cuentas.csv")) {
            reader.readLine();
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] c = linea.split(",", -1);
                resultado.add(new CuentaEntity(
                        Long.parseLong(c[0].trim()), Long.parseLong(c[1].trim()), c[2].trim(), c[3].trim(),
                        new BigDecimal(c[4].trim()), c[5].trim(), LocalDate.parse(c[6].trim())));
            }
        }
        return resultado;
    }

    private List<MovimientoEntity> leerMovimientos() throws Exception {
        List<MovimientoEntity> resultado = new ArrayList<>();
        try (BufferedReader reader = abrirLector("data/movimientos.csv")) {
            reader.readLine();
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] c = linea.split(",", -1);
                resultado.add(new MovimientoEntity(
                        null, Long.parseLong(c[0].trim()), LocalDate.parse(c[1].trim()), c[2].trim(),
                        new BigDecimal(c[3].trim()), c[4].trim()));
            }
        }
        return resultado;
    }

    private BufferedReader abrirLector(String classpathFile) throws Exception {
        InputStream input = new ClassPathResource(classpathFile).getInputStream();
        return new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    }
}

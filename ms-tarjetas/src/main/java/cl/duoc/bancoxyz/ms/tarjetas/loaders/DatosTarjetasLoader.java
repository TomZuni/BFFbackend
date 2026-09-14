package cl.duoc.bancoxyz.ms.tarjetas.loaders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import cl.duoc.bancoxyz.ms.tarjetas.entities.TarjetaEntity;
import cl.duoc.bancoxyz.ms.tarjetas.repositories.TarjetaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatosTarjetasLoader implements CommandLineRunner {

    private final TarjetaRepository tarjetaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (tarjetaRepository.count() > 0) {
            return;
        }
        List<TarjetaEntity> tarjetas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(abrirRecurso(), StandardCharsets.UTF_8))) {
            reader.readLine();
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] c = linea.split(",", -1);
                tarjetas.add(new TarjetaEntity(
                        Long.parseLong(c[0].trim()), c[1].trim(), Long.parseLong(c[2].trim()),
                        c[3].trim(), c[4].trim(), c[5].trim()));
            }
        }
        tarjetaRepository.saveAll(tarjetas);
        log.info(">> Datos legacy cargados: {} tarjetas", tarjetas.size());
    }

    private InputStream abrirRecurso() throws Exception {
        return new ClassPathResource("data/tarjetas.csv").getInputStream();
    }
}

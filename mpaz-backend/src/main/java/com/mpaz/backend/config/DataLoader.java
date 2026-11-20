package com.mpaz.backend.config;

import com.mpaz.backend.model.ObjetivoAprendizaje;
import com.mpaz.backend.repository.ObjetivoAprendizajeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(ObjetivoAprendizajeRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new ObjetivoAprendizaje("Lenguaje y Comunicación", "OA01", "Comprensión de Textos y Sustantivos", "Unidad 1", "Leer independientemente textos breves.", "PUBLICADO"));
                repo.save(new ObjetivoAprendizaje("Matemática", "OA02", "Conteo y Secuencias hasta 100", "Unidad 1", "Resolver secuencias numéricas básicas.", "PUBLICADO"));
                repo.save(new ObjetivoAprendizaje("Ciencias Naturales", "OA03", "Órganos del Cuerpo Humano", "Unidad 1", "Identificar corazón, pulmones y estómago.", "AUTORIZADO"));
                repo.save(new ObjetivoAprendizaje("Historia, Geografía y C. Sociales", "OA04", "Planos y Puntos Cardinales", "Unidad 1", "Ubicación espacial básica en planos.", "PUBLICADO"));
            }
        };
    }
}
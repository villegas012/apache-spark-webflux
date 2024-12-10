package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.service.SparkRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/api/spark-redis")
public class SparkRedisController {

    private static final Logger logger = LoggerFactory.getLogger(SparkRedisController.class);

    @Autowired
    private SparkRedisService sparkRedisService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostMapping("/merge")
    public Mono<String> mergeAndSaveJson(@RequestBody JsonNode[] jsons) {
        logger.info("Recibida solicitud para combinar y guardar JSON con {} elementos.", jsons.length);

        try {
            Future<String> resultFuture = executorService.submit(() -> {
                logger.info("Iniciando el proceso de combinación de JSONs en el controlador.");
                sparkRedisService.processAndSaveJson(jsons); // No bloqueo aquí para mantener asincronía
                return "JSON combinado y guardado en Redis con éxito.";
            });

            String result = resultFuture.get(); // Bloqueo solo para esperar el resultado
            return Mono.just(result);
        } catch (Exception e) {
            logger.error("Error al procesar y guardar JSON en Redis.", e);
            return Mono.just("Error al procesar JSON: " + e.getMessage());
        }
    }
}
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

@RestController
@RequestMapping("/api/spark-redis")
public class SparkRedisController {

    private static final Logger logger = LoggerFactory.getLogger(SparkRedisController.class);

    @Autowired
    private SparkRedisService sparkRedisService;

    @PostMapping("/merge")
    public Mono<String> mergeAndSaveJson(@RequestBody JsonNode[] jsons) {
        logger.info("Recibida solicitud para combinar y guardar JSON con {} elementos.", jsons.length);

        return Mono.fromCallable(() -> {
                    logger.info("Iniciando el proceso de combinación de JSONs en el controlador.");
                    sparkRedisService.processAndSaveJson(jsons).block(); // Bloqueo para asegurar ejecución
                    return "JSON combinado y guardado en Redis con éxito.";
                })
                .doOnNext(response -> logger.info("Respuesta generada: {}", response))
                .doOnError(e -> logger.error("Error en el flujo del controlador: {}", e.getMessage(), e))
                .onErrorResume(e -> {
                    logger.error("Flujo con error, enviando respuesta al cliente.");
                    return Mono.just("Error al procesar JSON: " + e.getMessage());
                });
    }
}
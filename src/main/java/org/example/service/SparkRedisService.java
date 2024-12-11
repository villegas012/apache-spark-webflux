package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.JsonMergeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@Service
public class SparkRedisService {

    private static final Logger logger = LoggerFactory.getLogger(SparkRedisService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<Void> processAndSaveJson(JsonNode[] jsons) {
        logger.info("processAndSaveJson: Inicio del método con {} JSONs.", jsons.length);

        try {
            JsonNode combinedNode = null;
            for (JsonNode currentNode : jsons) {
                logger.debug("JSON actual: {}", currentNode);
                combinedNode = combinedNode == null
                        ? currentNode
                        : JsonMergeHelper.merge(combinedNode, currentNode);
                logger.debug("JSON combinado parcial: {}", combinedNode);
            }

            String combinedJson = objectMapper.writeValueAsString(combinedNode);
            logger.info("JSON combinado generado: {}", combinedJson);

            CompletableFuture<Void> future = new CompletableFuture<>();
            reactiveRedisTemplate.opsForValue()
                    .set("combinedJson", combinedJson)
                    .doOnSuccess(aVoid -> {
                        logger.info("JSON guardado exitosamente en Redis.");
                        future.complete(null);
                    })
                    .doOnError(e -> {
                        logger.error("Error durante el procesamiento del JSON.", e);
                        future.completeExceptionally(e);
                    }).subscribe();
            future.get(); // Bloqueo para esperar la operación

            return Mono.empty();
        } catch (Exception e) {
            logger.error("Error al procesar JSON en Redis.", e);
            return Mono.error(new RuntimeException("Error al procesar JSON", e));
        }
    }

    public Mono<String> getValue(String key) {
        return reactiveRedisTemplate.opsForValue()
                .get(key)
                .doOnSuccess(value -> logger.info("Valor recuperado exitosamente de Redis para la clave: {}", key))
                .doOnError(error -> logger.error("Error al recuperar valor de Redis", error));
    }
}
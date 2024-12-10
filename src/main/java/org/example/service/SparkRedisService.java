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

@Service
public class SparkRedisService {

    private static final Logger logger = LoggerFactory.getLogger(SparkRedisService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    public Mono<Void> processAndSaveJson(JsonNode[] jsons) {
        logger.info("processAndSaveJson: Inicio del método con {} JSONs.", jsons.length);

        return Mono.fromCallable(() -> {
                    logger.info("Comenzando combinación de JSONs.");
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
                    return combinedJson;
                })
                .flatMap(combinedJson -> {
                    logger.info("Guardando JSON combinado en Redis con clave 'combinedJson'.");
                    return reactiveRedisTemplate.opsForValue().set("combinedJson", combinedJson);
                })
                .doOnSuccess(success -> logger.info("JSON guardado exitosamente en Redis."))
                .doOnError(e -> logger.error("Error durante el procesamiento del JSON.", e))
                .then()
                .doFinally(signal -> logger.info("processAndSaveJson: Fin del método con señal: {}", signal));
    }
}
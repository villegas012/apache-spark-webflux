package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.service.SparkRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spark-redis")
public class SparkRedisController {

    @Autowired
    private SparkRedisService sparkRedisService;

    @PostMapping("/merge")
    public String mergeAndSaveJson(@RequestBody JsonNode[] jsons) {
        try {
            sparkRedisService.processAndSaveJson(jsons);
            return "JSON combinado y guardado en Redis con éxito.";
        } catch (Exception e) {
            return "Error al procesar JSON: " + e.getMessage();
        }
    }
}

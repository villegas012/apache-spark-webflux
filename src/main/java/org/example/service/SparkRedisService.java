package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.JsonMergeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class SparkRedisService {

    @Autowired
    private ObjectMapper objectMapper;

    public void processAndSaveJson(JsonNode[] jsons) throws Exception {
        JsonNode combinedNode = null;

        // Combinar múltiples JSONs
        for (JsonNode currentNode : jsons) {
            combinedNode = combinedNode == null
                    ? currentNode
                    : JsonMergeHelper.merge(combinedNode, currentNode);
        }

        // Convertir el resultado combinado a JSON
        String combinedJson = objectMapper.writeValueAsString(combinedNode);

        // Guardar en Redis
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.set("combinedJson", combinedJson);
        }
    }
}

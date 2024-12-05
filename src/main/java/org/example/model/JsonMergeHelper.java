package org.example.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonMergeHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
        ObjectNode merged = MAPPER.createObjectNode();

        // Combinar campos de ambos JSON
        merged.setAll((ObjectNode) mainNode);
        merged.setAll((ObjectNode) updateNode);

        // Combinar arrays manualmente
        if (mainNode.has("phoneNumbers") && updateNode.has("phoneNumbers")) {
            ArrayNode phoneNumbers = MAPPER.createArrayNode();
            phoneNumbers.addAll((ArrayNode) mainNode.get("phoneNumbers"));
            phoneNumbers.addAll((ArrayNode) updateNode.get("phoneNumbers"));
            merged.set("phoneNumbers", phoneNumbers);
        }

        return merged;
    }
}

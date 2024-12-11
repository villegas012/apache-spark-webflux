package org.example.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * Helper para la combinación de objetos JSON.
 * Proporciona métodos para combinar nodos JSON y arrays JSON.
 */
public class JsonMergeHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Combina dos nodos JSON.
     *
     * @param mainNode El nodo JSON principal.
     * @param updateNode El nodo JSON con las actualizaciones.
     * @return Un nuevo nodo JSON que resulta de la combinación de los dos nodos.
     */
    public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
        ObjectNode merged = MAPPER.createObjectNode();
        merged.setAll((ObjectNode) mainNode);

        updateNode.fieldNames().forEachRemaining(fieldName -> {
            JsonNode updateValue = updateNode.get(fieldName);

            if (mainNode.has(fieldName)) {
                JsonNode mainValue = mainNode.get(fieldName);

                if (mainValue.isObject() && updateValue.isObject()) {
                    merged.set(fieldName, merge(mainValue, updateValue));
                } else if (mainValue.isArray() && updateValue.isArray()) {
                    ArrayNode combinedArray = combineArrays((ArrayNode) mainValue, (ArrayNode) updateValue);
                    merged.set(fieldName, combinedArray);
                } else {
                    merged.set(fieldName, mainValue);
                }
            } else {
                merged.set(fieldName, updateValue);
            }
        });

        return merged;
    }

    /**
     * Combina dos arrays JSON.
     *
     * @param mainArray El array JSON principal.
     * @param updateArray El array JSON con las actualizaciones.
     * @return Un nuevo array JSON que resulta de la combinación de los dos arrays.
     */
    private static ArrayNode combineArrays(ArrayNode mainArray, ArrayNode updateArray) {
        ArrayNode combinedArray = MAPPER.createArrayNode();
        combinedArray.addAll(mainArray);

        updateArray.forEach(item -> {
            boolean exists = false;
            for (JsonNode existingItem : mainArray) {
                if (existingItem.equals(item)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                combinedArray.add(item);
            }
        });

        return combinedArray;
    }
}
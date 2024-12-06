package org.example.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

//public class JsonMergeHelper {
//
//    private static final ObjectMapper MAPPER = new ObjectMapper();
//
//    public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
//        ObjectNode merged = MAPPER.createObjectNode();
//
//        // Combinar campos de ambos JSON
//        merged.setAll((ObjectNode) mainNode);
//        merged.setAll((ObjectNode) updateNode);
//
//        // Combinar arrays manualmente
//        if (mainNode.has("phoneNumbers") && updateNode.has("phoneNumbers")) {
//            ArrayNode phoneNumbers = MAPPER.createArrayNode();
//            phoneNumbers.addAll((ArrayNode) mainNode.get("phoneNumbers"));
//            phoneNumbers.addAll((ArrayNode) updateNode.get("phoneNumbers"));
//            merged.set("phoneNumbers", phoneNumbers);
//        }
//
//        return merged;
//    }
//}


public class JsonMergeHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

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

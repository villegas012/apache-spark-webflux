package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        // Crear SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("untitled")
                .master("local[*]")
                .config("spark.ui.enabled", "false") // Deshabilitar Spark UI para evitar conflictos
                .getOrCreate();

        // Crear JavaSparkContext
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        // Consumir y combinar JSON
        String json1 = "{ \"id\": 1, \"name\": \"John Doe\", \"age\": 30, \"email\": \"john.doe@example.com\", \"address\": { \"street\": \"123 Main St\", \"city\": \"New York\", \"state\": \"NY\", \"zip\": \"10001\" }, \"phoneNumbers\": [ { \"type\": \"home\", \"number\": \"212-555-1234\" }, { \"type\": \"work\", \"number\": \"646-555-5678\" } ], \"preferences\": { \"newsletter\": true, \"notifications\": { \"email\": true, \"sms\": false } } }";
        String json2 = "{ \"id\": 5, \"name\": \"Jane Smith\", \"age\": 25, \"email\": \"jane.smith@example.com\", \"address\": { \"street\": \"456 Elm St\", \"city\": \"Los Angeles\", \"state\": \"CA\", \"zip\": \"90001\" }, \"phoneNumbers\": [ { \"type\": \"mobile\", \"number\": \"310-555-7890\" } ], \"preferences\": { \"newsletter\": false, \"notifications\": { \"email\": true, \"sms\": true } }, \"employment\": { \"company\": \"Tech Solutions\", \"position\": \"Software Engineer\", \"startDate\": \"2020-06-15\" } }";

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node1 = mapper.readTree(json1);
            JsonNode node2 = mapper.readTree(json2);

            // Fusionar JSON
            JsonNode combinedNode = merge(node1, node2);

            // Convertir el resultado a JSON
            String combinedJson = mapper.writeValueAsString(combinedNode);

            // Guardar en Redis
            System.out.println("Intentando guardar en Redis...");
            try (Jedis jedis = new Jedis("localhost", 6379)) {
                jedis.set("combinedJson", combinedJson);
                System.out.println("JSON combinado guardado en Redis: " + combinedJson);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Detener SparkSession
        spark.stop();
    }

    private static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode merged = mapper.createObjectNode();

        // Combinar todos los campos de ambos JSON
        merged.setAll((ObjectNode) mainNode);
        merged.setAll((ObjectNode) updateNode);

        // Combinar arrays manualmente
        if (mainNode.has("phoneNumbers") && updateNode.has("phoneNumbers")) {
            ArrayNode phoneNumbers = mapper.createArrayNode();
            phoneNumbers.addAll((ArrayNode) mainNode.get("phoneNumbers"));
            phoneNumbers.addAll((ArrayNode) updateNode.get("phoneNumbers"));
            merged.set("phoneNumbers", phoneNumbers);
        }

        return merged;
    }
}
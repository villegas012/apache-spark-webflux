package org.example.config;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Apache Spark para la aplicación.
 * Esta clase define los beans necesarios para la configuración y uso de Spark.
 */
@Configuration
public class SparkConfig {

    /**
     * Crea una sesión de Spark.
     *
     * @return SparkSession configurada con el nombre de la aplicación, el modo local y la interfaz de usuario deshabilitada.
     */
    @Bean
    public SparkSession sparkSession() {
        return SparkSession.builder()
                .appName("UntitledSparkRedisExample")
                .master("local[*]")
                .config("spark.ui.enabled", "false")
                .getOrCreate();
    }

    /**
     * Crea un contexto de Spark para Java.
     *
     * @param sparkSession La sesión de Spark.
     * @return JavaSparkContext configurado con el contexto de la sesión de Spark.
     */
    @Bean
    public JavaSparkContext javaSparkContext(SparkSession sparkSession) {
        return new JavaSparkContext(sparkSession.sparkContext());
    }
}
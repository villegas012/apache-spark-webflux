package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuración de Redis para la aplicación.
 * Esta clase define los beans necesarios para la conexión y el uso de Redis.
 */
@Configuration
public class RedisConfig {

    /**
     * Crea una fábrica de conexiones Lettuce para Redis.
     *
     * @return LettuceConnectionFactory configurada para conectarse a Redis en localhost:6379.
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    /**
     * Crea un ReactiveRedisTemplate para operaciones reactivas con Redis.
     *
     * @param factory La fábrica de conexiones reactivas de Redis.
     * @return ReactiveRedisTemplate configurado con un contexto de serialización de cadenas.
     */
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
                .<String, String>newSerializationContext(new StringRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }
}

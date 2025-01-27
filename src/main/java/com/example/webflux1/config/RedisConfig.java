package com.example.webflux1.config;

import com.example.webflux1.repository.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisConfig implements ApplicationListener<ApplicationReadyEvent> {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        reactiveRedisTemplate.opsForValue().get("1")
                .doOnSuccess(i -> log.info("Initialize to redis connection"))
                .doOnError((err) -> log.error("Faailed to initialize redis connection : {}", err.getMessage()))
                .subscribe();
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
    }

    private <T> ReactiveRedisTemplate<String, T> createReactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory, Class<T> type) {

        Jackson2JsonRedisSerializer<T> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper(), type);

        RedisSerializationContext<String, T> serializationContext = RedisSerializationContext
                .<String, T>newSerializationContext()
                .key(RedisSerializer.string())
                .value(jsonRedisSerializer)
                .hashKey(RedisSerializer.string())
                .hashValue(jsonRedisSerializer)
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    @Bean
    public ReactiveRedisTemplate<String, User> reactiveRedisUserTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return createReactiveRedisTemplate(connectionFactory, User.class);
    }

}

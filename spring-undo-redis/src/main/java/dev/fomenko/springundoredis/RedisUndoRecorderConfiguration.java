package dev.fomenko.springundoredis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fomenko.springundocore.service.EventRecorder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisUndoRecorderConfiguration {

    @Bean
    public EventRecorder redisEventRecorder(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisEventRecorder(stringRedisTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnNotWebApplication
    public ObjectMapper objectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

}

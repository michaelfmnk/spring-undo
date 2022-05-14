package dev.fomenko.springundoredis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.testcontainers.containers.GenericContainer;

import java.text.SimpleDateFormat;

@TestConfiguration
public class RedisEventRecorderTestConfiguration {
    private static final int REDIS_PORT = 6379;

    private final static GenericContainer<?> redis = new GenericContainer<>("redis:5.0.2")
            .withExposedPorts(REDIS_PORT);

    static {
        redis.start();
    }

    @Bean
    @Primary
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redis.getHost());
        configuration.setPort(redis.getMappedPort(REDIS_PORT));
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat());
        return objectMapper;
    }
}

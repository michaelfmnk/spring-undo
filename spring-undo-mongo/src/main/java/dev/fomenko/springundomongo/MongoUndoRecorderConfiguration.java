package dev.fomenko.springundomongo;

import dev.fomenko.springundocore.service.EventRecorder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoUndoRecorderConfiguration {

    @Bean
    public EventRecorder mongoEventRecorder(MongoTemplate mongoTemplate) {
        return new MongoEventRecorder(mongoTemplate);
    }

}

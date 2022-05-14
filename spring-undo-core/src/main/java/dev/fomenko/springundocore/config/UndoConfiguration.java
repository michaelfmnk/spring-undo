package dev.fomenko.springundocore.config;

import dev.fomenko.springundocore.Undo;
import dev.fomenko.springundocore.UndoEventListener;
import dev.fomenko.springundocore.property.UndoProperties;
import dev.fomenko.springundocore.service.ActionIdGenerator;
import dev.fomenko.springundocore.service.EventRecorder;
import dev.fomenko.springundocore.service.GuidActionIdGenerator;
import dev.fomenko.springundocore.service.ScheduledTasks;
import dev.fomenko.springundocore.service.TimeSupplier;
import dev.fomenko.springundocore.service.UndoService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(UndoProperties.class)
public class UndoConfiguration {

    @Bean
    public Undo undo(UndoService undoService) {
        return new Undo(undoService);
    }

    @Bean
    public UndoService undoService(ActionIdGenerator eventIdGenerator,
                                   UndoProperties undoProperties,
                                   TimeSupplier timeSupplier,
                                   EventRecorder eventRecorder,
                                   List<UndoEventListener<?>> eventListeners) {
        Map<Class<?>, List<UndoEventListener<?>>> listenersMap = eventListeners.stream()
                .collect(Collectors.groupingBy(UndoEventListener::getActionClass));

        return new UndoService(
                listenersMap,
                timeSupplier,
                eventIdGenerator,
                eventRecorder,
                undoProperties
        );
    }

    @Bean
    public ScheduledTasks undoScheduledTasks(UndoService undoService) {
        return new ScheduledTasks(undoService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ActionIdGenerator undoIdGenerator() {
        return new GuidActionIdGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public TimeSupplier undoTimeSupplier() {
        return LocalDateTime::now;
    }

}

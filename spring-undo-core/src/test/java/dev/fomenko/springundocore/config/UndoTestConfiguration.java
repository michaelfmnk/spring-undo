package dev.fomenko.springundocore.config;

import dev.fomenko.springundocore.UndoEventListener;
import dev.fomenko.springundocore.dto.TestDtoC;
import dev.fomenko.springundocore.dto.TestDtoA;
import dev.fomenko.springundocore.dto.TestDtoB;
import dev.fomenko.springundocore.service.TimeSupplier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@TestConfiguration
@PropertySource("classpath:test.properties")
@ComponentScan(basePackages = "dev.fomenko")
public class UndoTestConfiguration {

    @Bean
    public TimeSupplier timeSupplier() {
        return () -> LocalDateTime.now(Clock.fixed(Instant.ofEpochSecond(1551000792), ZoneId.of("UTC")));
    }

    // action listeners for several events

    @Component
    public static class FirstListenerA extends UndoEventListener<TestDtoA> {
        @Override
        public void onUndo(TestDtoA action) {
            System.err.println("FirstListenerA");
        }
    }

    @Component
    public static class SecondListenerA extends UndoEventListener<TestDtoA> {
        @Override
        public void onUndo(TestDtoA action) {
            System.err.println("SecondListenerA");
        }
    }

    @Component
    public static class FirstListenerB extends UndoEventListener<TestDtoB> {
        @Override
        public void onUndo(TestDtoB action) {
            System.err.println("FirstListenerB");
        }
    }

    @Component
    public static class SecondListenerB extends UndoEventListener<TestDtoB> {
        @Override
        public void onUndo(TestDtoB action) {
            System.err.println("SecondListenerB");
        }
    }

    @Component
    public static class FaultyListenerC extends UndoEventListener<TestDtoC> {
        @Override
        public void onUndo(TestDtoC action) {
            throw new RuntimeException("FaultyDtoListener");
        }
    }

    @Component
    public static class SecondListenerC extends UndoEventListener<TestDtoC> {
        @Override
        public void onUndo(TestDtoC action) {
            System.err.println("SecondListenerC");
        }
    }

    @Component
    public static class ThirdListenerC extends UndoEventListener<TestDtoC> {
        @Override
        public void onUndo(TestDtoC action) {
            System.err.println("ThirdListenerC");
        }
    }
}

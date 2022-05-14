package dev.fomenko.springundocore.config;

import dev.fomenko.springundocore.UndoEventListener;
import dev.fomenko.springundocore.dto.TestDtoA;
import dev.fomenko.springundocore.dto.TestDtoB;
import dev.fomenko.springundocore.service.TimeSupplier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.time.Clock;
import java.time.Duration;
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
    @Bean
    public FirstListenerA firstA() {
        return new FirstListenerA();
    }

    @Bean
    public SecondListenerA secondA() {
        return new SecondListenerA();
    }

    @Bean
    public FirstListenerB firstB() {
        return new FirstListenerB();
    }

    @Bean
    public SecondListenerB secondB() {
        return new SecondListenerB();
    }

    public static class FirstListenerA extends UndoEventListener<TestDtoA> {
        @Override
        public void onUndo(TestDtoA action) {
            System.err.println("FirstListenerA");
        }
    }

    public static class SecondListenerA extends UndoEventListener<TestDtoA> {
        @Override
        public void onUndo(TestDtoA action) {
            System.err.println("SecondListenerA");
        }
    }

    public static class FirstListenerB extends UndoEventListener<TestDtoB> {
        @Override
        public void onUndo(TestDtoB action) {
            System.err.println("FirstListenerB");
        }
    }

    public static class SecondListenerB extends UndoEventListener<TestDtoB> {
        @Override
        public void onUndo(TestDtoB action) {
            System.err.println("SecondListenerB");
        }
    }

}

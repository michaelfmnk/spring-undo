package dev.fomenko.springundocore.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UndoConfigurationHolder {
    private Supplier<LocalDateTime> timeSupplier;
    private Duration defaultExpiration;
}

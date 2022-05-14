package dev.fomenko.springundocore.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@ConfigurationProperties("undo")
public class UndoProperties {
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration defaultExpirationSec = Duration.ofMinutes(1);
}

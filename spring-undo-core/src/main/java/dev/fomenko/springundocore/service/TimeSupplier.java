package dev.fomenko.springundocore.service;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public interface TimeSupplier extends Supplier<LocalDateTime> {
}

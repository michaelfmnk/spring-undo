package dev.fomenko.springundocore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class ScheduledTasks {
    private final UndoService recordsService;

    @Scheduled(fixedDelay = 5_000)
    public void deleteExpiredRecords() {
        recordsService.deleteExpiredRecords();
    }
}

package dev.fomenko.springundocore.service;

import dev.fomenko.springundocore.dto.ActionRecord;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;

public interface EventRecorder {
    @Async
    void saveRecord(ActionRecord<?> actionRecord);

    List<ActionRecord<?>> getAllRecords();

    Optional<ActionRecord<?>> getRecordById(String recordId);

    boolean deleteRecordById(String recordId);
}

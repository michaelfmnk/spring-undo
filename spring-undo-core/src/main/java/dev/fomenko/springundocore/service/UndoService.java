package dev.fomenko.springundocore.service;

import dev.fomenko.springundocore.UndoEventListener;
import dev.fomenko.springundocore.UndoListenerInvocationException;
import dev.fomenko.springundocore.dto.ActionRecord;
import dev.fomenko.springundocore.property.UndoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@CommonsLog
@RequiredArgsConstructor
public class UndoService {
    private final Map<Class<?>, List<UndoEventListener<?>>> listeners;
    private final Supplier<LocalDateTime> timeSupplier;
    private final ActionIdGenerator idGenerator;
    private final EventRecorder eventRecorder;
    private final UndoProperties properties;


    public void deleteExpiredRecords() {
        LocalDateTime now = timeSupplier.get();
        List<ActionRecord<?>> records = eventRecorder.getAllRecords().stream()
                .filter(record -> now.isAfter(record.getExpiresAt()))
                .collect(Collectors.toList());
        for (ActionRecord<?> record : records) {
            boolean wasDeleted = eventRecorder.deleteRecordById(record.getRecordId());
            if (wasDeleted) {
                invokeListenersForRecord(record, UndoEventListener::onPersist);
            }
        }
    }

    public <T> String saveEventRecord(T action, Duration expirationTime) {
        Duration timeout = Optional.ofNullable(expirationTime)
                .orElse(properties.getDefaultExpirationSec());

        LocalDateTime expiresAt = timeSupplier.get().plus(timeout);
        String recordId = idGenerator.generateId();
        ActionRecord<T> record = ActionRecord.<T>builder()
                .action(action)
                .expiresAt(expiresAt)
                .recordId(recordId)
                .build();
        eventRecorder.saveRecord(record);
        return recordId;
    }

    public void invokeListenerByRecordId(String recordId) {
        Optional<ActionRecord<?>> recordOptional = eventRecorder.getRecordById(recordId);
        boolean wasDeleted = eventRecorder.deleteRecordById(recordId);
        if (!wasDeleted) {
            return;
        }
        recordOptional.ifPresent((record) -> invokeListenersForRecord(record, UndoEventListener::onUndo));
    }

    @SuppressWarnings("unchecked")
    private void invokeListenersForRecord(ActionRecord<?> record,
                                          BiConsumer<UndoEventListener<? super Object>, Object> methodReference) {
        Object action = record.getAction();
        List<UndoEventListener<?>> undoEventListeners = listeners.get(action.getClass());


        if (CollectionUtils.isEmpty(undoEventListeners)) {
            log.warn("There are no listeners for " + action.getClass());
            return;
        }

        var errors = new ArrayList<Throwable>();
        for (UndoEventListener<?> listener : undoEventListeners) {
            try {
                methodReference.accept((UndoEventListener<? super Object>) listener, action);
            } catch (Throwable e) {
                errors.add(e);
            }
        }

        if (!errors.isEmpty()) {
            throw new UndoListenerInvocationException("There were errors while invoking undo listeners", errors);
        }
    }
}

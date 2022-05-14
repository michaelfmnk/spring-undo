package dev.fomenko.springundocore;

import dev.fomenko.springundocore.service.UndoService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Duration;

@RequiredArgsConstructor
public class Undo {

    private final UndoService undoService;

    public <T> String publish(T action, Duration expirationTime) {
        Assert.notNull(action, "action can not be null!");
        Assert.notNull(expirationTime, "expirationTime can not be null!");
        return undoService.saveEventRecord(action, expirationTime);
    }

    public <T> String publish(T action) {
        Assert.notNull(action, "action can not be null!");
        return undoService.saveEventRecord(action, null);
    }

    public void undo(String recordId) {
        if (!StringUtils.hasLength(recordId)) {
            return;
        }
        undoService.invokeListenerByRecordId(recordId);
    }

}

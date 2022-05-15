package dev.fomenko.springundoredis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fomenko.springundocore.dto.ActionRecord;
import dev.fomenko.springundocore.service.EventRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@CommonsLog
@RequiredArgsConstructor
public class RedisEventRecorder implements EventRecorder {
    private static final String KEY_PREFIX = "recoverable-action-";
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;


    @Override
    public void saveRecord(ActionRecord<?> actionRecord) {
        try {
            String recordId = actionRecord.getRecordId();
            String serializedRecord = serializeRecord(actionRecord);
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + recordId, serializedRecord);
        } catch (IOException e) {
            log.error("Couldn't serialize record", e);
        }
    }

    @Override
    public Optional<ActionRecord<?>> getRecordById(String recordId) {
        String serializedRecord = stringRedisTemplate.opsForValue().get(KEY_PREFIX + recordId);
        if (Objects.nonNull(serializedRecord)) {
            try {
                return Optional.ofNullable(deserializeRecord(serializedRecord));
            } catch (IOException e) {
                log.error("there was an error during serialization", e);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ActionRecord<?>> getAllRecords() {
        ArrayList<ActionRecord<?>> records = new ArrayList<>();
        Set<String> keys = stringRedisTemplate.keys(KEY_PREFIX + "*");

        if (Objects.nonNull(keys)) {
            for (String key : keys) {
                try {
                    String serializedRecord = stringRedisTemplate.opsForValue().get(key);
                    records.add(deserializeRecord(serializedRecord));
                } catch (ClassCastException | IOException e) {
                    log.error("there was an error during serialization", e);
                }
            }
        }

        return records;
    }

    @Override
    public boolean deleteRecordById(String recordId) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(KEY_PREFIX + recordId));
    }

    private String serializeRecord(ActionRecord<?> recordEntity) throws JsonProcessingException {
        return objectMapper.writeValueAsString(RecordConverter.toEntity(recordEntity));
    }

    private ActionRecord<?> deserializeRecord(String serializedRecord) throws IOException {
        RecordEntity<?> recordEntity = objectMapper.readValue(serializedRecord, RecordEntity.class);
        return RecordConverter.toDto(recordEntity);
    }

}

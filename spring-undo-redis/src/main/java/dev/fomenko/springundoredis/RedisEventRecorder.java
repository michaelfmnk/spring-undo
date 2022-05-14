package dev.fomenko.springundoredis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fomenko.springundocore.dto.ActionRecord;
import dev.fomenko.springundocore.service.EventRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@CommonsLog
@RequiredArgsConstructor
public class RedisEventRecorder implements EventRecorder {

    private static final String HASH_NAME = "recoverable-actions-hash-name";
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public void saveEventRecord(String recordId, ActionRecord<?> actionRecord) {
        try {
            String serializedRecord = serializeRecord(actionRecord);
            stringRedisTemplate.opsForHash().put(HASH_NAME, recordId, serializedRecord);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't serialize an action", e);
        }
    }

    public Optional<ActionRecord<?>> getByRecordId(String recordId) {
        String serializedRecord = (String) stringRedisTemplate.opsForHash().get(HASH_NAME, recordId);
        if (Objects.isNull(serializedRecord)) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(deserializeRecord(serializedRecord));
        } catch (IOException e) {
            log.error("there was an error during serialization", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ActionRecord<?>> getAllRecords() {
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        List<ActionRecord<?>> resultRecords = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : opsForHash.entries(HASH_NAME).entrySet()) {
            try {
                resultRecords.add(deserializeRecord((String) entry.getValue()));
            } catch (ClassCastException | IOException e) {
                opsForHash.delete(HASH_NAME, entry.getKey());
            }
        }
        return resultRecords;
    }

    public boolean deleteByRecordId(String recordId) {
        Long deletedCount = stringRedisTemplate.opsForHash().delete(HASH_NAME, recordId);
        return Objects.equals(deletedCount, 1L);
    }

    private String serializeRecord(ActionRecord<?> recordEntity) throws JsonProcessingException {
        return objectMapper.writeValueAsString(RecordConverter.toEntity(recordEntity));
    }

    private ActionRecord<?> deserializeRecord(String serializedRecord) throws IOException {
        RecordEntity<?> recordEntity = objectMapper.readValue(serializedRecord, RecordEntity.class);
        return RecordConverter.toDto(recordEntity);
    }

}

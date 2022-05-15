package dev.fomenko.springundoredis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fomenko.springundocore.dto.ActionRecord;
import dev.fomenko.springundocore.service.EventRecorder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RedisEventRecorderTestConfiguration.class)
public class RedisEventRecorderTest {

    private static final String PREFIX = "recoverable-action-";
    @Autowired
    private EventRecorder eventRecorder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @AfterEach
    public void afterEach() {
        Objects.requireNonNull(stringRedisTemplate.getConnectionFactory()).getConnection().flushAll();
    }

    @Test
    @SneakyThrows
    public void shouldSaveRecord() {
        // given
        String recordId = "recordId-1";
        TestActionDto action = new TestActionDto("data1", 43);
        ActionRecord<?> record = new ActionRecord<>(recordId, action, fixedTime());

        // when
        eventRecorder.saveRecord(record);

        // then
        // assert exists
        Set<String> keys = Objects.requireNonNull(stringRedisTemplate.keys(PREFIX + "*"));
        Assertions.assertEquals(1, keys.size());
        Assertions.assertTrue(keys.contains(PREFIX + recordId));

        // assert record
        String serializedRecord = stringRedisTemplate.opsForValue().get(PREFIX + recordId);
        Assertions.assertNotNull(serializedRecord);
        RecordEntity<TestActionDto> recordInRedis = objectMapper.readValue(serializedRecord, RecordEntity.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(recordId, recordInRedis.getRecordId()),
                () -> Assertions.assertEquals(record.getExpiresAt(), recordInRedis.getExpiresAt()),
                () -> Assertions.assertEquals(action.getTestData(), recordInRedis.getAction().getTestData()),
                () -> Assertions.assertEquals(action.getNum(), recordInRedis.getAction().getNum())
        );
    }

    @Test
    @SneakyThrows
    public void shouldReturnAllRecordsFromRedis() {
        // given
        List<ActionRecord<TestActionDto>> records = Stream.of(
                        new ActionRecord<>("1", new TestActionDto("t1", 11), fixedTime()),
                        new ActionRecord<>("2", new TestActionDto("t2", 22), fixedTime()),
                        new ActionRecord<>("3", new TestActionDto("t3", 33), fixedTime()))
                .collect(Collectors.toList());

        // when
        for (ActionRecord<TestActionDto> testActionDtoActionRecord : records) {
            eventRecorder.saveRecord(testActionDtoActionRecord);
        }

        List<ActionRecord<?>> allRecords = eventRecorder.getAllRecords();

        // then
        Assertions.assertEquals(records.size(), allRecords.size());
        for (ActionRecord<?> record : allRecords) {
            ActionRecord<?> found = allRecords.stream()
                    .filter(item -> Objects.equals(record.getRecordId(), item.getRecordId()))
                    .findFirst().orElse(null);
            Assertions.assertEquals(found, record);
        }
    }

    private LocalDateTime fixedTime() {
        return LocalDateTime.of(2019, 3, 21, 7, 7);
    }

}

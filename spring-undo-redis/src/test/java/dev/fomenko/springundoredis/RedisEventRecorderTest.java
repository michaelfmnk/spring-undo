package dev.fomenko.springundoredis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fomenko.springundocore.dto.ActionRecord;
import dev.fomenko.springundocore.service.EventRecorder;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RedisEventRecorderTestConfiguration.class)
public class RedisEventRecorderTest {

    private static final String HASH_NAME = "recoverable-actions-hash-name";
    @Autowired
    private EventRecorder eventRecorder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @After
    public void prepare() {
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @SneakyThrows
    public void shouldSaveRecord() {
        // given
        String recordId = "recordId-1";
        TestActionDto action = new TestActionDto("data1", 43);
        ActionRecord<?> record = new ActionRecord<>(recordId, action, fixedTime());

        // when
        eventRecorder.saveEventRecord(recordId, record);

        // then
        Object objInRedis = stringRedisTemplate.opsForHash().get(HASH_NAME, recordId);
        RecordEntity<?> recordInRedis = objectMapper.readValue((String) objInRedis, RecordEntity.class);
        TestActionDto actionInRedis = (TestActionDto) recordInRedis.getAction();
        Assert.assertEquals(recordId, recordInRedis.getRecordId());
        Assert.assertEquals(action.getTestData(), actionInRedis.getTestData());
        Assert.assertEquals(record.getExpiresAt(), recordInRedis.getExpiresAt());
        Assert.assertEquals(action.getNum(), actionInRedis.getNum());
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
        for (int i = 0; i < records.size(); i++) {
            eventRecorder.saveEventRecord(String.valueOf(i), records.get(i));
        }

        List<ActionRecord<?>> allRecords = eventRecorder.getAllRecords();

        // them
        Assert.assertEquals(records.size(), allRecords.size());
        for (ActionRecord<?> record : allRecords) {
            ActionRecord<?> found = allRecords.stream()
                    .filter(item -> Objects.equals(record.getRecordId(), item.getRecordId()))
                    .findFirst().orElse(null);
            Assert.assertEquals(found, record);
        }
    }

    private LocalDateTime fixedTime() {
        return LocalDateTime.of(2019, 3, 21, 7, 7);
    }

}

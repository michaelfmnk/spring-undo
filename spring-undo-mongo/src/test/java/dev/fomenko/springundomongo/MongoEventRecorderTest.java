package dev.fomenko.springundomongo;

import dev.fomenko.springundocore.dto.ActionRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static dev.fomenko.springundomongo.MongoEventRecorder.COLLECTION_NAME;

@SpringBootTest
class MongoEventRecorderTest {
    @Autowired
    private MongoEventRecorder mongoEventRecorder;

    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    void setup() {
        mongoTemplate.dropCollection(COLLECTION_NAME);
    }


    @Test
    void shouldSaveRecordCorrectly() {
        //given
        String recordId = "record_name";
        LocalDateTime expiresAt = LocalDateTime.now();
        String actionName = "action1";
        String description = "desc";
        int size = 382;

        ActionRecord<TestDto> actionTest = ActionRecord.<TestDto>builder()
                .recordId(recordId)
                .action(TestDto.builder()
                        .testName(actionName)
                        .testDescription(description)
                        .size(size).build())
                .expiresAt(expiresAt).build();

        //when
        mongoEventRecorder.saveRecord(actionTest);

        //then
        List<RecordEntity> recordEntities = mongoTemplate.findAll(RecordEntity.class, COLLECTION_NAME);
        RecordEntity<TestDto> record = recordEntities.get(0);

        Assertions.assertEquals(1, recordEntities.size());
        Assertions.assertEquals(actionName, record.getAction().getTestName());
        Assertions.assertEquals(description, record.getAction().getTestDescription());
        Assertions.assertEquals(size, record.getAction().getSize());
        Assertions.assertEquals(recordId, record.getRecordId());
    }


    @Test
    void shouldGetAllRecords() {
        //given
        LocalDateTime expiresAt1 = LocalDateTime.now();
        String recordId1 = "event1";
        String testName1 = "name1";
        ActionRecord<TestDto> actionTest1 = ActionRecord.<TestDto>builder()
                .recordId(recordId1)
                .action(TestDto.builder()
                        .testName(testName1)
                        .testDescription("desc")
                        .size(382).build())
                .expiresAt(expiresAt1).build();


        LocalDateTime expiresAt2 = LocalDateTime.now();
        String recordId2 = "event2";
        String testName2 = "name2";
        ActionRecord<TestDto> actionTest2 = ActionRecord.<TestDto>builder()
                .recordId(recordId2)
                .action(TestDto.builder()
                        .testName(testName2)
                        .testDescription("desc")
                        .size(382).build())
                .expiresAt(expiresAt2).build();

        //when
        mongoTemplate.save(MongoConverter.toEntity(actionTest1), COLLECTION_NAME);
        mongoTemplate.save(MongoConverter.toEntity(actionTest2), COLLECTION_NAME);

        //then
        List<ActionRecord<?>> allRecords = mongoEventRecorder.getAllRecords();
        Assertions.assertEquals(2, allRecords.size());

        Assertions.assertEquals(recordId1, allRecords.get(0).getRecordId());
        Assertions.assertEquals(testName1, ((TestDto) allRecords.get(0).getAction()).getTestName());

        Assertions.assertEquals(recordId2, allRecords.get(1).getRecordId());
        Assertions.assertEquals(testName2, ((TestDto) allRecords.get(1).getAction()).getTestName());
    }


    @Test
    void shouldGetRecordById() {
        //given
        String description = "description01";
        String recordId = "record01";
        int size = 2;
        String testName = "test01";

        ActionRecord<TestDto> actionTest1 = ActionRecord.<TestDto>builder()
                .recordId(recordId)
                .action(TestDto.builder()
                        .testName(testName)
                        .testDescription(description)
                        .size(size)
                        .build())
                .build();

        ActionRecord<TestDto> actionTest2 = ActionRecord.<TestDto>builder()
                .recordId("record02")
                .action(TestDto.builder()
                        .testDescription("description02")
                        .size(12)
                        .build())
                .build();

        mongoTemplate.save(MongoConverter.toEntity(actionTest1), COLLECTION_NAME);
        mongoTemplate.save(MongoConverter.toEntity(actionTest2), COLLECTION_NAME);

        //when
        Optional<ActionRecord<?>> recordById = mongoEventRecorder.getRecordById(recordId);

        //then
        ActionRecord<TestDto> resultRecord = (ActionRecord<TestDto>) recordById.get();

        Assertions.assertEquals(recordId, resultRecord.getRecordId());
        Assertions.assertEquals(testName, resultRecord.getAction().getTestName());
        Assertions.assertEquals(description, resultRecord.getAction().getTestDescription());
        Assertions.assertEquals(size, resultRecord.getAction().getSize());
    }


    @Test
    void shouldDeleteRecordById() {
        //given
        String recordId1 = "event1";
        ActionRecord<TestDto> actionTest1 = ActionRecord.<TestDto>builder()
                .recordId(recordId1)
                .action(TestDto.builder().build())
                .expiresAt(LocalDateTime.now()).build();

        String recordId2 = "event2";
        ActionRecord<TestDto> actionTest2 = ActionRecord.<TestDto>builder()
                .recordId(recordId2)
                .action(TestDto.builder().build())
                .expiresAt(LocalDateTime.now()).build();

        mongoTemplate.save(MongoConverter.toEntity(actionTest1), COLLECTION_NAME);
        mongoTemplate.save(MongoConverter.toEntity(actionTest2), COLLECTION_NAME);

        //when
        boolean wasDeleted = mongoEventRecorder.deleteRecordById(recordId2);


        //then
        List<RecordEntity> results = mongoTemplate.findAll(RecordEntity.class, COLLECTION_NAME);

        Assertions.assertTrue(wasDeleted);
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(recordId1, results.get(0).getRecordId());
    }

}
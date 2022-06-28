package dev.fomenko.springundomongo;


import com.mongodb.client.result.DeleteResult;
import dev.fomenko.springundocore.dto.ActionRecord;
import dev.fomenko.springundocore.service.EventRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@CommonsLog
@RequiredArgsConstructor
public class MongoEventRecorder implements EventRecorder {
    public static final String COLLECTION_NAME = "recoverableEvents";

    private final MongoTemplate mongoTemplate;


    @Override
    public void saveRecord(ActionRecord<?> actionRecord) {
        RecordEntity<?> recordEntity = MongoConverter.toEntity(actionRecord);
        mongoTemplate.save(recordEntity, COLLECTION_NAME);
    }


    @Override
    public List<ActionRecord<?>> getAllRecords() {
        List<RecordEntity> results = mongoTemplate.findAll(RecordEntity.class, COLLECTION_NAME);
        return results.stream()
                .map(MongoConverter::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public Optional<ActionRecord<?>> getRecordById(String recordId) {
        Query query = new Query(Criteria.where("recordId").is(recordId));
        RecordEntity<?> recordEntity = mongoTemplate.findOne(query, RecordEntity.class, COLLECTION_NAME);
        return Optional.of(MongoConverter.toDto(recordEntity));
    }

    @Override
    public boolean deleteRecordById(String recordId) {
        Query query = new Query(Criteria.where("recordId").is(recordId));
        DeleteResult deleteResult = mongoTemplate.remove(query, RecordEntity.class, COLLECTION_NAME);
        long deletedCount = deleteResult.getDeletedCount();
        return deletedCount > 0;
    }

}

package dev.fomenko.springundomongo;

import dev.fomenko.springundocore.dto.ActionRecord;

public final class MongoConverter {

    private MongoConverter() {
    }

    public static RecordEntity<?> toEntity(ActionRecord<?> dto) {
        return RecordEntity.builder()
                .action(dto.getAction())
                .expiresAt(dto.getExpiresAt())
                .recordId(dto.getRecordId())
                .build();
    }


    public static ActionRecord<?> toDto(RecordEntity<?> dto) {
        return ActionRecord.builder()
                .action(dto.getAction())
                .expiresAt(dto.getExpiresAt())
                .recordId(dto.getRecordId())
                .build();
    }
}

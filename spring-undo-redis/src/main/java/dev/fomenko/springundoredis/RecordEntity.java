package dev.fomenko.springundoredis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordEntity<T> {
    private String recordId;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    private T action;
    private LocalDateTime expiresAt;
}

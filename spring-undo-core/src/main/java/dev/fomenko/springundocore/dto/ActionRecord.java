package dev.fomenko.springundocore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionRecord<T> {
    private String recordId;
    private T action;
    private LocalDateTime expiresAt;
}

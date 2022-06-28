package dev.fomenko.springundomongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDto implements Serializable {
    private String testName;
    private String testDescription;
    private Integer size;
}

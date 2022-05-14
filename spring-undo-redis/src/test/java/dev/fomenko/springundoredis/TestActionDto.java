package dev.fomenko.springundoredis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestActionDto implements Serializable {
    private String testData;
    private Integer num;
}

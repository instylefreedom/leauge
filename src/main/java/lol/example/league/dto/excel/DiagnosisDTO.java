package lol.example.league.dto.excel;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class DiagnosisDTO {

    private String type;
    private String result;
    private LocalDateTime time;
}

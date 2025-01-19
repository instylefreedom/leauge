package lol.example.league.dto.response;

import lol.example.league.dto.MatchResult;
import lol.example.league.entity.GameResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class MatchResponse {

    private Long    logId;
    private Integer playDate;
    private Integer season;
    private Integer round;
    private Integer set;
    private GameResult result;
    private Integer team;
    private Long    userId;


}

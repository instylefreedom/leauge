package lol.example.league.dto.response;

import lol.example.league.entity.GameResult;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {
    private Integer playDate;
    private Integer season;
    private Integer round;
    private String set1;
    private String set2;
    private String set3;
    private String player1;
    private String player2;
    private String player3;
    private String player4;
    private String player5;
    private String player6;
    private String player7;
    private String player8;
    private String player9;
    private String player10;


}

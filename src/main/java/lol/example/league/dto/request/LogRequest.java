package lol.example.league.dto.request;

import lol.example.league.dto.MatchResult;
import lol.example.league.entity.GameResult;
import lombok.Getter;

import java.util.List;

@Getter
public class LogRequest {

    private String gameDate;
    private Integer season;
    private Integer round;
//    private Integer set;
//    private GameResult team1;
//    private GameResult team2;
    private Integer set1;
    private Integer set2;
    private Integer set3;
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

//    private Integer gameId;
//    private MatchResult match;

}

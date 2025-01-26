package lol.example.league.dto.response;

import lol.example.league.dto.matchmake.UserMatchMake;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserMatchResponse {

    private Integer team1Total;
    private Integer team2Total;
    private final List<UserMatchMake> team1;
    private final List<UserMatchMake> team2;
}

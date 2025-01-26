package lol.example.league.dto.matchmake;

import lol.example.league.entity.User;
import lol.example.league.entity.board.MatchMakingEntity;
import lol.example.league.repository.MatchMapping;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMatchMake {

    private Long userId;
    private String userName;
    private Long rating;
    private String mainLane;
    private String subLane;
    private String tier;
    private Integer bracket;


    public static UserMatchMake from (MatchMakingEntity user) {
        return UserMatchMake.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .rating(user.getRating())
                .mainLane(user.getMainLane())
                .subLane(user.getSubLane())
                .tier(user.getTier())
                .bracket(user.getBracket())
                .build();
    }

    public static UserMatchMake of (MatchMapping user) {
        return UserMatchMake.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .rating(user.getRating())
                .mainLane(user.getMainLane())
                .subLane(user.getSubLane())
                .tier(user.getTier())
                .bracket(user.getBracket())
                .build();
    }

}

package lol.example.league.dto.request;

import lombok.Getter;

@Getter
public class UserRequest {
    private String userName;
    private String userEmail;
//    private Integer tier;
    private String mainLane;
    private String subLane;
    private long userId;

}

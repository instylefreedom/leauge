package lol.example.league.dto.response;

import lol.example.league.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UserResponse {
    private Long   userId;
    private String userName;
    private String tier;
    private String mainLane;
    private String subLane;
    private Integer matchNumber;
    private Integer win;
    private Integer loss;
    private String winRate;
    private Integer winPoint;
    private String picture;
    private Integer cwin;
    private Integer close;
    private Integer rating;


//    public UserResponse(User user, Integer matchNumber, Integer win, Integer loss, String winRate, Integer winPoint){
//        this.userId = user.getUserId();
//        this.userName = user.getUserName();
//        this.tier = user.getTier();
//        this.mainLane = user.getMainLane();
//        this.subLane = user.getSubLane();
//        this.matchNumber = matchNumber;
//        this.win = win;
//        this.loss = loss;
//        this.winRate = winRate;
//        this.winPoint = winPoint;
//    }

    public UserResponse(User user){
        this.userId = user.getUserId();
        if(user.getUserName()==null){
            this.userName = "";
        }
        else {
            this.userName = user.getUserName();
        }
        if(user.getTier()==null){
            this.tier = "";
        }
        else{
            this.tier = String.valueOf(user.getTier());
        }
        if(user.getMainLane()==null){
            this.mainLane = "";
        }
        else{
            this.mainLane = user.getMainLane();
        }
        if(user.getSubLane()==null){
            this.subLane = "";
        }
        else{
            this.subLane = user.getSubLane();
        }
        this.picture= user.getPicture();
        this.rating = user.getRating();
    }
}

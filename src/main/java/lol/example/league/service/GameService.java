package lol.example.league.service;

import lol.example.league.dto.response.GameResponse;
import lol.example.league.dto.response.MatchResponse;
import lol.example.league.entity.GameLog;
import lol.example.league.entity.GameResult;
import lol.example.league.repository.GameLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameLogRepository repository;
    public List<Map<String,Object>> getGameData() {

       return repository.gameList();
    }

    public GameResponse getGameDataDetail(Integer round, Integer date) {

        List<Map<String,Object>> game = repository.findGameDetailInfo(date,round) ;
        GameResponse response = new GameResponse();
        response.setPlayDate(date);
        response.setSeason((Integer) game.get(0).get("season"));
        response.setRound(round);
        if(game.size()==1){
            response.setSet1(game.get(0).get("team") +"팀 승리");
            response.setSet2("");
            response.setSet3("");
        }
        else if(game.size()==2){
            response.setSet1(game.get(0).get("team") +"팀 승리");
            response.setSet2(game.get(1).get("team")+"팀 승리");
            response.setSet3("");
        }
        else if(game.size()==3){
            response.setSet1(game.get(0).get("team") +"팀 승리");
            response.setSet2(game.get(1).get("team")+"팀 승리");
            response.setSet3(game.get(2).get("team")+"팀 승리");
        }

        List<String> player = repository.findGamePlayer(date,round);
        if(player.get(0) == null){response.setPlayer1("불명확한 유저");}else{response.setPlayer1(player.get(0));}
        if(player.get(1) == null){response.setPlayer2("불명확한 유저");}else{response.setPlayer2(player.get(1));}
        if(player.get(2) == null){response.setPlayer3("불명확한 유저");}else{response.setPlayer3(player.get(2));}
        if(player.get(3) == null){response.setPlayer4("불명확한 유저");}else{response.setPlayer4(player.get(3));}
        if(player.get(4) == null){response.setPlayer5("불명확한 유저");}else{response.setPlayer5(player.get(4));}
        if(player.get(5) == null){response.setPlayer6("불명확한 유저");}else{response.setPlayer6(player.get(5));}
        if(player.get(6) == null){response.setPlayer7("불명확한 유저");}else{response.setPlayer7(player.get(6));}
        if(player.get(7) == null){response.setPlayer8("불명확한 유저");}else{response.setPlayer8(player.get(7));}
        if(player.get(8) == null){response.setPlayer9("불명확한 유저");}else{response.setPlayer9(player.get(8));}
        if(player.get(9) == null){response.setPlayer10("불명확한 유저");}else{response.setPlayer10(player.get(9));}

        return response;

    }

//    public List<Map<String, Object>> getGameDataDetail(Long roundId, String playDate) {
//
//        repository.findByRoundAndPlayDate()
//    }
}

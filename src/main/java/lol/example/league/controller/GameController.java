package lol.example.league.controller;

import lol.example.league.dto.response.MatchResponse;
import lol.example.league.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GameController {

    private final GameService gmaeService;

    /**
     * index controller 에서 내전 기록 조회에서 서비스 사용중
     * @return
     */
    @GetMapping("/api/game/get/data")
    public List<Map<String,Object>> getGameData() {

        return gmaeService.getGameData();
    }

//    @GetMapping("/api/game/get/data/detail")
//    public List<Map<String,Object>> getGameData(@RequestParam  Long roundId, String playDate) {
//
//        return gmaeService.getGameDataDetail(roundId, playDate);
//    }
}

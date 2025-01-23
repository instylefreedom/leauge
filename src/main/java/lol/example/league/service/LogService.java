package lol.example.league.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lol.example.league.config.auth.dto.SessionUser;
import lol.example.league.dto.request.LogRequest;
import lol.example.league.dto.response.MatchResponse;
import lol.example.league.entity.GameLog;
import lol.example.league.entity.GameResult;
import lol.example.league.entity.Role;
import lol.example.league.entity.User;
import lol.example.league.repository.GameLogRepository;
import lol.example.league.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

//    private final GoogleApiUtil googleApiUtil;
    private final GameLogRepository repository;
    private final UserService service;
    private final UserRepository userRepository;
    private final GameLogRepository logRepository;

    private final Integer SILVER = 10;
    private final Integer MASTER = 60;
    private final Integer OTHER = 99;


    public List<MatchResponse> getLogData(Integer roundId) {
        List<GameLog> logs = repository.findByRound(roundId);
        List<MatchResponse> response = new ArrayList<>();
        for(GameLog data : logs){
            MatchResponse rdata = MatchResponse.builder()
                    .logId(data.getLogId())
                    .playDate(data.getPlayDate())
                    .season(data.getSeason())
                    .round(data.getRound())
                    .set(data.getSet())
                    .result(data.getResult())
                    .team(data.getTeam())
                    .userId(data.getUserId())
                    .build();
            response.add(rdata);
        }
        return response;
    }

    @Transactional
    public String saveLogData(LogRequest request, SessionUser userInfo) throws Exception {
        User admin = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(() -> new EntityNotFoundException("no user data"));
        if (admin.getRole()== Role.ADMIN) {
            String gameDate = request.getGameDate();
            Integer season = request.getSeason();
            Integer round = request.getRound();
//        Integer gameId = request.getGameId();
//        MatchResult match = request.getMatch();
            GameLog maxGame = repository.findLatestGame();
            Integer gameId = maxGame.getGameId() + 1;
            List<String> player = new ArrayList<>();
//        MatchResult result = new MatchResult();

            //세트 결과 세팅
            List<Integer> setResult = new ArrayList<>();
            if(!ObjectUtils.isEmpty(request.getSet1())) setResult.add(request.getSet1());
            if(!ObjectUtils.isEmpty(request.getSet2())) setResult.add(request.getSet2());
            if(!ObjectUtils.isEmpty(request.getSet3())) setResult.add(request.getSet3());

            //플레이어 세팅
            player.add(request.getPlayer1());
            player.add(request.getPlayer2());
            player.add(request.getPlayer3());
            player.add(request.getPlayer4());
            player.add(request.getPlayer5());
            player.add(request.getPlayer6());
            player.add(request.getPlayer7());
            player.add(request.getPlayer8());
            player.add(request.getPlayer9());
            player.add(request.getPlayer10());

            int setCount = 1;
            GameResult team1 = null;
            GameResult team2 = null;
            for(Integer set:setResult) {
                Integer valid = repository.findDupeGame(Integer.valueOf(gameDate), season, round, setCount);
                if(valid == 0){
                    if(set == 1){
                        team1 = GameResult.WIN;
                        team2 = GameResult.LOSE;
                    }
                    else if(set ==2){
                        team1 = GameResult.LOSE;
                        team2 = GameResult.WIN;
                    }
                    for (int i = 0; i < 5; i++) {
                        User user = service.findByName(player.get(i));
                        GameLog log = GameLog.builder()
                                .playDate(Integer.valueOf(gameDate))
                                .season(season)
                                .round(round)
                                .set(setCount)
                                .result(team1)
                                .team(1)
                                .userId(user.getUserId())
                                .gameId(gameId)
                                .createdAt(LocalDateTime.now())
                                .createdBy(admin.getUserId())
                                .build();
                        repository.save(log);
                        calculateUserRating(user, team1);
                    }

                    for (int k = 5; k < 10; k++) {
                        User user = service.findByName(player.get(k));
                        GameLog log = GameLog.builder()
                                .playDate(Integer.valueOf(gameDate))
                                .season(season)
                                .round(round)
                                .set(setCount)
                                .result(team2)
                                .team(2)
                                .userId(user.getUserId())
                                .gameId(gameId)
                                .createdAt(LocalDateTime.now())
                                .createdBy(admin.getUserId())
                                .build();
                        repository.save(log);
                        calculateUserRating(user, team2);
                    }
                    gameId ++;
                    setCount ++;
                }
                else {
                    log.info("중복 내전 결과 저장 시도 확인! 날짜: {} 라운드: {} 세트: {}", request.getGameDate(), request.getRound(), setCount);
                    return (String.format("중복 내전 결과 저장 시도 확인! 날짜: %s 라운드: %s 세트: %s", request.getGameDate(), request.getRound(), setCount));
                }
            }

        }
        else {
            log.info("관리자 권한 없는 접근 시도 {}", admin.getName());
            return (String.format("관리자 권한 없는 접근 시도 %s", admin.getName()));
        }
//        round ++;

        return null;
    }
    public void calculateUserRating(User user, GameResult result) {
        // 점수제 기반 +- 처리
        // 브~골 + 1티어 점수 변동
        if(user.getRating() <= 1980){
            if((user.getRating()>=1560 && user.getRating()<=1580) || (user.getRating()>=1660 && user.getRating()<=1680) || (user.getRating()>=1760 && user.getRating()<=1780)) {
                if(result.equals(GameResult.WIN)) {
                    user.setRating(user.getRating()+2);
                }
                else{
                    user.setRating(user.getRating()-4);
                }
            }
            else {
                if(result.equals(GameResult.WIN)) {
                    user.setRating(user.getRating()+3);
                }
                else{
                    user.setRating(user.getRating()-2);
                }
            }
        }

        // 플레 이상
        else {
            if(result.equals(GameResult.WIN)) {
                user.setRating(user.getRating()+4);
            }
            else{
                user.setRating(user.getRating()-2);
            }
        }


    }
// 2025/1/19 티어 연승 연패가 아닌 점수재로 작업하기로 결정
//            int cwin = 0;
//            int close = 0;
//            // 티어 업데이트 작업 시작
//            for(String p : player){
//                User user = service.findByName(p);
//                // 연승패 계산
//                List<Map<String,String>> roundResult = logRepository.getConsecutiveRound(user.getUserId(), Long.valueOf(season));
//                if (roundResult.size() > 0) {
//                    String result = roundResult.get(0).get("result");
//                    for(Map<String,String> rounds : roundResult){
//                        if(rounds.get("result").equals(result)){
//                            if(rounds.get("result").equals(GameResult.LOSE.toString())){
//                                close+=1;
//                            }else if(rounds.get("result").equals(GameResult.WIN.toString())){
//                                cwin+=1;
//                            }
//                        }
//                        else{
//                            break;
//                        }
//                    }
//                }
//
//                // 연승패에 따른 티어 변경 작업
//                if(cwin > 0 && cwin % 4 == 0){
//                    switch (user.getTier()){
//                        //실버
//                        case 10 :
//                            user.updateTier(20,4);
//                            break;
//                        //골드
//                        case 20 :
//                            if(user.getBracket().equals(1))
//                                user.updateTier(30,4);
//                            else if(user.getBracket().equals(2))
//                                user.updateTier(20,1);
//                            else if(user.getBracket().equals(3))
//                                user.updateTier(20,2);
//                            else if(user.getBracket().equals(4))
//                                user.updateTier(20,3);
//                            break;
//                        //플레
//                        case 30 :
//                            if(user.getBracket().equals(1))
//                                user.updateTier(40,4);
//                            else if(user.getBracket().equals(2))
//                                user.updateTier(30,1);
//                            else if(user.getBracket().equals(3))
//                                user.updateTier(30,2);
//                            else if(user.getBracket().equals(4))
//                                user.updateTier(30,3);
//                            break;
//                        //에메
//                        case 40 :
//                            if(user.getBracket().equals(1))
//                                user.updateTier(50,4);
//                            else if(user.getBracket().equals(2))
//                                user.updateTier(40,1);
//                            else if(user.getBracket().equals(3))
//                                user.updateTier(40,2);
//                            else if(user.getBracket().equals(4))
//                                user.updateTier(40,3);
//                            break;
//                        //다이아
//                        case 50 :
//                            if(user.getBracket().equals(1))
//                                user.updateTier(60,null);
//                            else if(user.getBracket().equals(2))
//                                user.updateTier(50,1);
//                            else if(user.getBracket().equals(3))
//                                user.updateTier(50,2);
//                            else if(user.getBracket().equals(4))
//                                user.updateTier(50,3);
//                            break;
//
//                    }
//                }
//                else if (close > 0 && close % 4 == 0){
//                    switch (user.getTier()) {
//                        //골드
//                        case 20:
//                            if (user.getBracket().equals(1))
//                                user.updateTier(20, 2);
//                            else if (user.getBracket().equals(2))
//                                user.updateTier(20, 3);
//                            else if (user.getBracket().equals(3))
//                                user.updateTier(20, 4);
//                            else if (user.getBracket().equals(4))
//                                user.updateTier(10, 1);
//                            break;
//                        //플레
//                        case 30:
//                            if (user.getBracket().equals(1))
//                                user.updateTier(30, 2);
//                            else if (user.getBracket().equals(2))
//                                user.updateTier(30, 3);
//                            else if (user.getBracket().equals(3))
//                                user.updateTier(30, 4);
//                            else if (user.getBracket().equals(4))
//                                user.updateTier(20, 1);
//                            break;
//                        //에메
//                        case 40:
//                            if (user.getBracket().equals(1))
//                                user.updateTier(40, 2);
//                            else if (user.getBracket().equals(2))
//                                user.updateTier(40, 3);
//                            else if (user.getBracket().equals(3))
//                                user.updateTier(40, 4);
//                            else if (user.getBracket().equals(4))
//                                user.updateTier(30, 1);
//                            break;
//                        //다이아
//                        case 50:
//                            if (user.getBracket().equals(1))
//                                user.updateTier(50, 2);
//                            else if (user.getBracket().equals(2))
//                                user.updateTier(50, 3);
//                            else if (user.getBracket().equals(3))
//                                user.updateTier(50, 4);
//                            else if (user.getBracket().equals(4))
//                                user.updateTier(40, 1);
//                            break;
//                        case 60:
//                            user.updateTier(50, 1);
//                            break;
//                    }
//                }
//
//                cwin = 0;
//                close = 0;
//            }



    @Transactional
    public String deleteLogLatest(SessionUser userInfo) {
        User admin = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(() -> new EntityNotFoundException("no user data"));
        if (admin.getRole()== Role.ADMIN) {
            GameLog LatestGame = repository.findLatestGame();
            repository.deleteAllByPlayDateAndRound(LatestGame.getPlayDate(),LatestGame.getRound());
            return "최근 전적 삭제 완료.";
        }
        else {
            log.info("관리자 권한 없는 접근 시도 {}", admin.getName());
            return (String.format("관리자 권한 없는 접근 시도 %s", admin.getName()));
        }

    }

//    public String saveLogData() throws GeneralSecurityException, IOException {
//        return googleApiUtil.saveLogData();
//    }

//    public String getLogData(Integer date, Integer round, Integer set) throws GeneralSecurityException, IOException{
//    }
//
//    public String updateLogData(Integer logId) throws GeneralSecurityException, IOException{
//    }
//
//    public String deleteLogData() throws GeneralSecurityException, IOException{
//    }
}

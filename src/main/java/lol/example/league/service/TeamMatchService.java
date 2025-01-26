package lol.example.league.service;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import lol.example.league.dto.matchmake.UserMatchMake;
import lol.example.league.dto.response.MatchResponse;
import lol.example.league.dto.response.UserMatchResponse;
import lol.example.league.entity.User;
import lol.example.league.entity.board.MatchMakingEntity;
import lol.example.league.repository.MatchMakingRepository;
import lol.example.league.repository.MatchMapping;
import lol.example.league.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMatchService {

    private final UserRepository userRepository;
    private final MatchMakingRepository matchMakingRepository;

    public void matchUser(List<String> userList) throws Exception {

        Integer difference = 100;
        Integer finalTeam1Score = 0;
        Integer finalTeam2Score = 0;
        List<UserMatchMake> finalTeam1 = new ArrayList<>();
        List<UserMatchMake> finalTeam2 = new ArrayList<>();
        // 유저 10명 조회
        List<MatchMapping> userInfoList = userRepository.getMatchUser(userList);

        if(userInfoList.size() != 10) {
            throw new Exception("매치 메이킹 인원이 10명이 안됩니다. 입렵 인원을 확인해 주세요");
        }

        for(int i = 0; i < 1000; i++) {
            // 10명 랜덤 셔플 후 순서대로 팀을 나눔
            Collections.shuffle(userInfoList);
            List<MatchMapping> tempTeam1 = userInfoList.subList(0,5);
            List<MatchMapping> tempTeam2 = userInfoList.subList(5,10);

            List<UserMatchMake> mapTeam1 = tempTeam1.stream().map(UserMatchMake::of).collect(Collectors.toList());
            List<UserMatchMake> mapTeam2 = tempTeam2.stream().map(UserMatchMake::of).collect(Collectors.toList());



            Integer team1Score = mapTeam1.stream().mapToInt(m -> Math.toIntExact(m.getRating())).sum();
            Integer team2Score = mapTeam2.stream().mapToInt(m -> Math.toIntExact(m.getRating())).sum();
            Integer teamDifference = Math.abs(team1Score-team2Score);
            // 팀간 차이가 75 이하고 이전 75점 이하 조합보다 좋으면 update
            if(teamDifference <= 75 || teamDifference < difference) {
                difference = teamDifference;
                finalTeam1 = mapTeam1;
                finalTeam2 = mapTeam2;
//                finalTeam1Score = team1Score;
//                finalTeam2Score = team2Score;
                //팀차이가 0 이면 종료
                if(teamDifference == 0) {
                    break;
                }
            }

        }

        List<MatchMakingEntity> entity1 = finalTeam1.stream().map(t -> MatchMakingEntity.builder()
                .userId(t.getUserId())
                .team("team1")
                .userName(t.getUserName())
                .bracket(t.getBracket())
                .tier(t.getTier())
                .rating(t.getRating())
                .mainLane(t.getMainLane())
                .subLane(t.getSubLane())
                .build()
        ).toList();
        matchMakingRepository.saveAll(entity1);
        List<MatchMakingEntity> entity2 = finalTeam2.stream().map(t -> MatchMakingEntity.builder()
                .userId(t.getUserId())
                .team("team2")
                .userName(t.getUserName())
                .bracket(t.getBracket())
                .tier(t.getTier())
                .rating(t.getRating())
                .mainLane(t.getMainLane())
                .subLane(t.getSubLane())
                .build()
        ).toList();
        matchMakingRepository.saveAll(entity2);

    }

    public String getRule() {
        Random rnd = new Random();
        Integer random = rnd.nextInt(5);
        String rule = null;

        switch (random) {
            case 0:
                rule = "토너먼트 드래프트 (밴가드 0)";
                break;
            case 1:
                rule = "토너먼트 드래프트 (밴가드 X)";
                break;
            case 2:
                rule = "하드피어리스 (밴가드 0)";
                break;
            case 3:
                rule = "하드피어리스 (밴가드 X)";
                break;
            case 4:
                rule = "토너먼트 드래프트 (3라운드 블라인드)";
                break;
        }

        return rule;
    }

    public UserMatchResponse getMatchResult() {

        List<MatchMakingEntity> entityList = matchMakingRepository.findAll();

        List<MatchMakingEntity> entity1 = entityList.stream().filter(e -> e.getTeam().equals("team1")).collect(Collectors.toList());
        List<MatchMakingEntity> entity2 = entityList.stream().filter(e -> e.getTeam().equals("team2")).collect(Collectors.toList());

        Integer team1Score = entity1.stream().mapToInt(e -> Math.toIntExact(e.getRating())).sum()/5;
        Integer team2Score = entity2.stream().mapToInt(e -> Math.toIntExact(e.getRating())).sum()/5;
        matchMakingRepository.deleteAll();
        return UserMatchResponse.builder()
                .team1(entity1.stream().map(UserMatchMake::from).collect(Collectors.toList()))
                .team2(entity2.stream().map(UserMatchMake::from).collect(Collectors.toList()))
                .team1Total(team1Score)
                .team2Total(team2Score)
                .build();

    }



}

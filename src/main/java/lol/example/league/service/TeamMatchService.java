package lol.example.league.service;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import lol.example.league.dto.matchmake.UserMatchMake;
import lol.example.league.dto.response.MatchResponse;
import lol.example.league.dto.response.UserMatchResponse;
import lol.example.league.entity.User;
import lol.example.league.entity.board.MatchMakingEntity;
import lol.example.league.repository.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamMatchService {

    private final UserRepository userRepository;
    private final MatchMakingRepository matchMakingRepository;
//    private final afpRepository repository;
//    private final afpFinalRepository finalRepository;

    public void matchUser(List<String> userList) throws Exception {

        Long difference = 100L;
//        Integer finalTeam1Score = 0;
//        Integer finalTeam2Score = 0;
        List<UserMatchMake> finalTeam1 = new ArrayList<>();
        List<UserMatchMake> finalTeam2 = new ArrayList<>();
        // 유저 10명 조회
        List<MatchMapping> userInfoList = userRepository.getMatchUser(userList);
        Map<Long, MatchMapping> userInfoMap = userInfoList.stream().collect(Collectors.toMap(u -> u.getUserId(), u -> u));

        if(userInfoList.size() != 10) {
            throw new Exception("매치 메이킹 인원이 10명이 안됩니다. 입렵 인원을 확인해 주세요");
        }
        List<List<Long>> teamCombinations = new ArrayList<>();
        List<Long> userIdList = userInfoList.stream().map(u -> u.getUserId()).collect(Collectors.toList());
        generateCombinations(userIdList, 5, 0, new ArrayList<>(), teamCombinations);
        Map<Long, Long> ratingMap = userInfoList.stream().collect(Collectors.toMap( u -> u.getUserId(), u -> u.getRating()));
        log.info("조합 숫자: " + teamCombinations.size());

        for(List<Long> comb : teamCombinations) {
            List<Long> tempTeam1 = comb;
            List<Long> tempTeam2 = new ArrayList<>(userIdList);
            tempTeam2.removeAll(tempTeam1);

            Long team1Total = tempTeam1.stream().map( t -> ratingMap.get(t)).collect(Collectors.toList()).stream().mapToLong(m -> m).sum();
            Long team2Total = tempTeam2.stream().map( t -> ratingMap.get(t)).collect(Collectors.toList()).stream().mapToLong(m -> m).sum();
            Long teamDifference = Math.abs(team1Total-team2Total);
            // 팀간 차이가 75 이하고 이전 75점 이하 조합보다 좋으면 update
            if(teamDifference <= 75 || teamDifference < difference) {
                difference = teamDifference;
                finalTeam1 = tempTeam1.stream().map(t -> UserMatchMake.of(userInfoMap.get(t))).collect(Collectors.toList());
                finalTeam2 = tempTeam2.stream().map(t -> UserMatchMake.of(userInfoMap.get(t))).collect(Collectors.toList());

                //팀차이가 0 이면 종료
                if(teamDifference == 0) {
                    break;
                }
            }
        }

//        for(int i = 0; i < 1000; i++) {
//                // 10명 랜덤 셔플 후 순서대로 팀을 나눔
//                Collections.shuffle(userInfoList);
//                List<MatchMapping> tempTeam1 = userInfoList.subList(0,5);
//                List<MatchMapping> tempTeam2 = userInfoList.subList(5,10);
//
//                List<UserMatchMake> mapTeam1 = tempTeam1.stream().map(UserMatchMake::of).collect(Collectors.toList());
//                List<UserMatchMake> mapTeam2 = tempTeam2.stream().map(UserMatchMake::of).collect(Collectors.toList());
//
//
//
//                Integer team1Score = mapTeam1.stream().mapToInt(m -> Math.toIntExact(m.getRating())).sum();
//                Integer team2Score = mapTeam2.stream().mapToInt(m -> Math.toIntExact(m.getRating())).sum();
//                Integer teamDifference = Math.abs(team1Score-team2Score);
//                // 팀간 차이가 75 이하고 이전 75점 이하 조합보다 좋으면 update
//                if(teamDifference <= 75 || teamDifference < difference) {
//                    difference = teamDifference;
//                    finalTeam1 = mapTeam1;
//                    finalTeam2 = mapTeam2;
////                finalTeam1Score = team1Score;
////                finalTeam2Score = team2Score;
//                    //팀차이가 0 이면 종료
//                    if(teamDifference == 0) {
//                        break;
//                    }
//                }
//
//        }

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

    // 조합 생성 함수
    public static void combine(List<Integer> arr, int r, int start, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == r) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < arr.size(); i++) {
            current.add(arr.get(i));
            combine(arr, r, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private static void generateCombinations(List<Long> teamList, int teamSize, int start,
                                             List<Long> current, List<List<Long>> result) {
        if (current.size() == teamSize) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < teamList.size(); i++) {
            current.add(teamList.get(i));
            generateCombinations(teamList, teamSize, i + 1, current, result);
            current.remove(current.size() - 1); // 백트래킹
        }
    }

    public String getRule() {
        Random rnd = new Random();
        int random = rnd.nextInt(100);  // 0 ~ 99
        String rule = null;

        if (random < 30) { // 30%
            rule = "하드피어리스 (벤 목록 포함)";
        } else if (random < 55) { // 25%
            rule = "하드피어리스 (밴가드 X)";
        }  else if (random < 80) { // 25%
            rule = "하드피어리스 (밴가드 0)";
        } else { // 나머지 20%
            rule = "토너먼트 드래프트";
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
    @Getter
    static class User {
        String id;
        int score;

        User(String id, int score) {
            this.id = id;
            this.score = score;
        }

        @Override
        public String toString() {
            return id + "(" + score + ")";
        }
    }

    static class Result {
        List<User> teamA;
        List<User> teamB;
        int diff;

        Result(List<User> teamA, List<User> teamB, int diff) {
            this.teamA = teamA;
            this.teamB = teamB;
            this.diff = diff;
        }
    }
    public void matchUser2(List<String> userList)  throws Exception {

        Long difference = 100L;
        List<UserMatchMake> finalTeam1 = new ArrayList<>();
        List<UserMatchMake> finalTeam2 = new ArrayList<>();

        // 유저 10명 조회
        List<MatchMapping> userInfoList = userRepository.getMatchUser(userList);
        Map<String, MatchMapping> userInfoMap = userInfoList.stream().collect(Collectors.toMap(u -> u.getUserName(), u -> u));

        if(userInfoList.size() != 10) {
            throw new Exception("매치 메이킹 인원이 10명이 안됩니다. 입렵 인원을 확인해 주세요");
        }

        List<User> users = userInfoList.stream()
                .map(u -> new User(u.getUserName(), Math.toIntExact(u.getRating())))
                .collect(Collectors.toList());
        int n = users.size();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < n; i++) indices.add(i);

        List<List<Integer>> allTeamA = new ArrayList<>();
        combine(indices, 5, 0, new ArrayList<>(), allTeamA);

        Set<String> seen = new HashSet<>();
        Result bestResult = null;

        for (List<Integer> teamAIndices : allTeamA) {
            List<Integer> teamBIndices = new ArrayList<>(indices);
            teamBIndices.removeAll(teamAIndices);

            List<Integer> sortedA = new ArrayList<>(teamAIndices);
            List<Integer> sortedB = new ArrayList<>(teamBIndices);
            Collections.sort(sortedA);
            Collections.sort(sortedB);
            String key = sortedA.toString() + "|" + sortedB.toString();
            String keyReversed = sortedB.toString() + "|" + sortedA.toString();
            if (seen.contains(key) || seen.contains(keyReversed)) continue;
            seen.add(key);

            List<User> teamA = new ArrayList<>();
            List<User> teamB = new ArrayList<>();
            int scoreA = 0, scoreB = 0;

            for (int idx : teamAIndices) {
                User user = users.get(idx);
                teamA.add(user);
                scoreA += user.score;
            }

            for (int idx : teamBIndices) {
                User user = users.get(idx);
                teamB.add(user);
                scoreB += user.score;
            }

            int diff = Math.abs(scoreA - scoreB);

            if (bestResult == null || diff < bestResult.diff) {
                bestResult = new Result(teamA, teamB, diff);
            }
        }


        difference = (long) bestResult.diff;
        finalTeam1 = bestResult.teamA.stream().map(t -> UserMatchMake.of(userInfoMap.get(t.id))).collect(Collectors.toList());
        finalTeam2 = bestResult.teamB.stream().map(t -> UserMatchMake.of(userInfoMap.get(t.id))).collect(Collectors.toList());

        System.out.println("🔵 Team A: " + bestResult.teamA);
        System.out.println("🔴 Team B: " + bestResult.teamB);
        System.out.println("📊 Score Difference: " + difference);

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
}

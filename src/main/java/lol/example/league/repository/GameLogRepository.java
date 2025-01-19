package lol.example.league.repository;

import lol.example.league.entity.GameLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface GameLogRepository extends JpaRepository<GameLog, Long> {

    List<GameLog> findByRound(Integer roundId);

    @Query(value= "select p from GameLog as p order by p.gameId desc limit 1")
    GameLog findLatestGame();

    @Query(value= "select count(p) from GameLog as p where p.playDate = :date and p.season = :season and p.round = :round and p.set = :set")
    Integer findDupeGame(@Param("date") Integer date, @Param("season") Integer season, @Param("round") Integer round, @Param("set") Integer set );


    @Query(value="select g.playDate as playDate, g.season as season, g.round as round, g.set as set, g.team as team, g.result as result from GameLog as g where g.playDate = :date and g.round = :round and g.result='WIN' group by g.playDate, g.season, g.round, g.set, g.result order by g.set")
    List<Map<String,Object>> findGameDetailInfo(@Param("date") Integer date, @Param("round") Integer round);

    @Query(value = "select u.userName from GameLog g left join User u on g.userId = u.userId where g.playDate = :date and g.round = :round and g.set = 1 order by g.team")
    List<String> findGamePlayer(@Param("date") Integer date, @Param("round") Integer round);

    @Query(value = "SELECT \n" +
            "r.play_date,\n" +
            "r.season,\n" +
            "r.`round`,\n" +
            "r.result,\n" +
            "s.set1,\n" +
            "s.set2,\n" +
            "s.set3,\n" +
            "u.user_name \n" +
            "FROM \n" +
            "(\n" +
            "\tSELECT \n" +
            "\t\tplay_date,\n" +
            "\t\tseason,\n" +
            "\t\t`round`,\n" +
            "\t\tcase when sum(team1) > sum(team2) then '1팀 승리' else \"2팀 승리\" end as result,\n" +
            "\t\tcreated_by\n" +
            "\tfrom \n" +
            "\t(\n" +
            "\t\tSELECT \n" +
            "\t\t\tplay_date,\n" +
            "\t\t\tseason,\n" +
            "\t\t\t`round`,\n" +
            "\t\t\tcase when result = \"WIN\" and team = 1 then 1 else 0 end as team1,\n" +
            "\t\t\tcase when result = \"WIN\" and team = 2 then 1 else 0 end  as team2,\n" +
            "\t\t\tcreated_by\n" +
            "\t\tFROM game_log glt \n" +
            "\t) h \n" +
            "\tgroup by \n" +
            "\tplay_date,season,`round`,created_by\n" +
            ") r\n" +
            "left join\n" +
            "(\n" +
            "\tSELECT \n" +
            "\t\tplay_date,\n" +
            "\t\tseason,\n" +
            "\t\t`round`,\n" +
            "\t\tcase when sum(set1t1) > 0 and sum(set1t1) > sum(set1t2) then '1팀 승리' \n" +
            "\t\t \t when sum(set1t2) > 0 and sum(set1t2) > sum(set1t1) then '2팀 승리'\n" +
            "\t\t\t else \"\" end as set1,\n" +
            " \t\tcase when sum(set2t1) > 0 and sum(set2t1) > sum(set2t2) then '1팀 승리' \n" +
            "\t\t \t when sum(set2t2) > 0 and sum(set2t2) > sum(set2t1) then '2팀 승리'\n" +
            "\t\t\t else \"\" end as set2,\n" +
            "  \t\tcase when sum(set3t1) > 0 and sum(set3t1) > sum(set3t2) then '1팀 승리' \n" +
            "\t\t \t when sum(set3t2) > 0 and sum(set3t2) > sum(set3t1) then '2팀 승리'\n" +
            "\t\t\t else \"\" end as set3\n" +
            "\tfrom\n" +
            "\t(\n" +
            "\t\tSELECT \n" +
            "\t\t\tplay_date,\n" +
            "\t\t\tseason,\n" +
            "\t\t\t`round`,\n" +
            "\t\t\t`set`,\n" +
            "\t\t\tgame_id,\n" +
            "\t\t\tcase when result = \"WIN\" and team = 1 and `set` = 1 then 1 else 0 end as set1t1,\n" +
            "\t\t\tcase when result = \"WIN\" and team = 2 and `set` = 1 then 1 else 0 end as set1t2,\n" +
            "\t\t\tcase when result = \"WIN\" and team = 1 and `set` = 2 then 1 else 0 end as set2t1,\n" +
            "\t\t\tcase when result = \"WIN\" and team = 2 and `set` = 2 then 1 else 0 end as set2t2,\n" +
            "\t\t\tcase when result = \"WIN\" and team = 1 and `set` = 3 then 1 else 0 end as set3t1,\n" +
            "\t\t\tcase when result = \"WIN\" and team = 2 and `set` = 3 then 1 else 0 end as set3t2\n" +
            "\t\tFROM game_log glt \n" +
            "\t) s\n" +
            "\tgroup by play_date, season,`round`\n" +
            ") s \n" +
            "on r.play_date = s.play_date and r.season = s.season and r.`round` = s.`round`\n" +
            "left join `user` u on u.user_id = r.created_by  \n" +
            "order by r.play_date desc, `round` desc;", nativeQuery = true)
    List<Map<String,Object>> gameList();

//    GameLog findTop1ByUserIdOrderByGameIdDesc(Long userId);

//    List<GameLog> findByUserIdOrderByGameIdDesc(Long userId);

    GameLog findTop1ByUserIdAndSeasonOrderByGameIdDesc(Long userId, Long season);

    List<GameLog> findByUserIdAndSeasonOrderByGameIdDesc(Long userId, Long season);

    @Query(value="SELECT \n" +
            "r.play_date,\n" +
            "r.season,\n" +
            "r.`round`,\n" +
            "r.result\n" +
            "FROM \n" +
            "(\n" +
            "\tSELECT \n" +
            "\t\tplay_date,\n" +
            "\t\tseason,\n" +
            "\t\t`round`,\n" +
            "\t\tcase when sum(win) > sum(lose) then 'WIN' else \"LOSE\" end as result\n" +
            "\tfrom \n" +
            "\t(\n" +
            "\t\tSELECT \n" +
            "\t\t\tplay_date,\n" +
            "\t\t\tseason,\n" +
            "\t\t\t`round`,\n" +
            "\t\t\t`result`,\n" +
            "\t\t\tcase when result = \"WIN\" then 1 else 0 end as win,\n" +
            "\t\t\tcase when result = \"LOSE\" then 1 else 0 end as lose\n" +
            "\t\tFROM game_log glt \n" +
            "\t\twhere glt.season = :season \n" +
            "\t\tand glt.user_id = :userId \n" +
            "\t) h \n" +
            "\tgroup by \n" +
            "\tplay_date,season,`round`\n" +
            ") r\n" +
            "order by r.play_date desc, `round` desc;", nativeQuery = true)
    List<Map<String,String>> getConsecutiveRound(@Param("userId") Long userId, @Param("season") Long season);

    void deleteAllByPlayDateAndRound(Integer playDate, Integer round);
}

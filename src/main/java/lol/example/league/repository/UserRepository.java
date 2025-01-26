package lol.example.league.repository;

import lol.example.league.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    User findByUserName(String name);

    @Query(value="SELECT u.userName FROM User as u")
    List<String> getAllUserName();

    @Query(value="SELECT \n" +
                "  u.user_id \n" +
                ", u.user_name \n" +
//                ", t.tier_name tier \n" +
                ",case when rating <= 1580 then '브론즈' \n" +
                "     when rating > 1580 and rating <= 1680 then '실버' \n" +
                "     when rating > 1680 and rating <= 1780 then '골드' \n" +
                "     when rating > 1780 and rating <= 1900 then '플레티넘' \n" +
                "     when rating > 1900 and rating <= 2100 then '다이아몬드' \n" +
                "     when rating > 2100 and rating <= 2250 then '마스터' \n" +
                "     when rating > 2250  then '챌린저' end as tier \n"  +
//                ", u.bracket \n" +
                ",case when rating >= 1480 and rating <= 1500 then 5 \n" +
                "     when rating > 1500 and rating <= 1520 then 4 \n" +
                "     when rating > 1520 and rating <= 1540 then 3 \n" +
                "     when rating > 1540 and rating <= 1560 then 2 \n" +
                "     when rating > 1560 and rating <= 1580 then 1 \n" +
                "     when rating > 1580 and rating <= 1600 then 5 \n" +
                "     when rating > 1600 and rating <= 1620 then 4 \n" +
                "     when rating > 1620 and rating <= 1640 then 3 \n" +
                "     when rating > 1640 and rating <= 1660 then 2 \n" +
                "     when rating > 1660 and rating <= 1680 then 1 \n" +
                "     when rating > 1680 and rating <= 1700 then 5 \n" +
                "     when rating > 1700 and rating <= 1720 then 4 \n" +
                "     when rating > 1720 and rating <= 1740 then 3 \n" +
                "     when rating > 1740 and rating <= 1760 then 2 \n" +
                "     when rating > 1760 and rating <= 1780 then 1 \n" +
                "     when rating > 1780 and rating <= 1800 then 5 \n" +
                "     when rating > 1800 and rating <= 1820 then 4 \n" +
                "     when rating > 1820 and rating <= 1860 then 3 \n" +
                "     when rating > 1860 and rating <= 1880 then 2 \n" +
                "     when rating > 1880 and rating <= 1900 then 1 \n" +
                "     when rating > 1900 and rating <= 1920 then 5 \n" +
                "     when rating > 1920 and rating <= 1940 then 4 \n" +
                "     when rating > 1940 and rating <= 1960 then 3 \n" +
                "     when rating > 1960 and rating <= 1980 then 2 \n" +
                "     when rating > 1980 and rating <= 2100 then 1  else null end as bracket \n" +
                ", u.main_lane \n" +
                ", u.sub_lane \n" +
                ", u.rating  \n" +
                ", ifnull(g.matchCount,0) matchCount\n" +
                ", ifnull(g.winCount,0) winCount\n" +
                ", ifnull(g.loseCount,0) loseCount\n" +
                ", round(g.winCount/g.matchCount,4)*100 as rate\n" +
                ", ifnull(g.winCount,0)*5 - ifnull(g.loseCount,0)*3 as point\n" +
                "from \n" +
                "`user` u \n" +
                "left outer join \n" +
                "(\n" +
                "SELECT \n" +
                "  user_id \n" +
                ", count(*) as matchCount\n" +
                ", sum(case when result = 'WIN' then 1 else 0 end) as winCount\n" +
                ", sum(case when result = 'LOSE' then 1 else 0 end) as loseCount\n" +
                "from game_log gl \n" +
                "where season = :season \n" +
                "group by user_id) g\n" +
                "on u.user_id = g.user_id\n" +
//                "left outer join tier t on u.tier = t.tier_id\n" +
                "order by g.winCount*5 - g.loseCount*3 desc, g.matchCount desc", nativeQuery = true)
    List<Map<String,String>> getDashboard(@Param("season") Long season);

    List<User> findAllByUserIdIn(List<Integer> userList);

    @Query(value="SELECT \n" +
            "  u.user_id as userId \n" +
            ", u.user_name as userName \n" +
            ",case when rating <= 1580 then '브론즈' \n" +
            "     when rating > 1580 and rating <= 1680 then '실버' \n" +
            "     when rating > 1680 and rating <= 1780 then '골드' \n" +
            "     when rating > 1780 and rating <= 1900 then '플레티넘' \n" +
            "     when rating > 1900 and rating <= 2100 then '다이아몬드' \n" +
            "     when rating > 2100 and rating <= 2250 then '마스터' \n" +
            "     when rating > 2250  then '챌린저' end as tier \n"  +
            ",case when rating >= 1480 and rating <= 1500 then 5 \n" +
            "     when rating > 1500 and rating <= 1520 then 4 \n" +
            "     when rating > 1520 and rating <= 1540 then 3 \n" +
            "     when rating > 1540 and rating <= 1560 then 2 \n" +
            "     when rating > 1560 and rating <= 1580 then 1 \n" +
            "     when rating > 1580 and rating <= 1600 then 5 \n" +
            "     when rating > 1600 and rating <= 1620 then 4 \n" +
            "     when rating > 1620 and rating <= 1640 then 3 \n" +
            "     when rating > 1640 and rating <= 1660 then 2 \n" +
            "     when rating > 1660 and rating <= 1680 then 1 \n" +
            "     when rating > 1680 and rating <= 1700 then 5 \n" +
            "     when rating > 1700 and rating <= 1720 then 4 \n" +
            "     when rating > 1720 and rating <= 1740 then 3 \n" +
            "     when rating > 1740 and rating <= 1760 then 2 \n" +
            "     when rating > 1760 and rating <= 1780 then 1 \n" +
            "     when rating > 1780 and rating <= 1800 then 5 \n" +
            "     when rating > 1800 and rating <= 1820 then 4 \n" +
            "     when rating > 1820 and rating <= 1860 then 3 \n" +
            "     when rating > 1860 and rating <= 1880 then 2 \n" +
            "     when rating > 1880 and rating <= 1900 then 1 \n" +
            "     when rating > 1900 and rating <= 1920 then 5 \n" +
            "     when rating > 1920 and rating <= 1940 then 4 \n" +
            "     when rating > 1940 and rating <= 1960 then 3 \n" +
            "     when rating > 1960 and rating <= 1980 then 2 \n" +
            "     when rating > 1980 and rating <= 2100 then 1  else null end as bracket \n" +
            ", u.main_lane \n" +
            ", u.sub_lane \n" +
            ", u.rating  \n" +
            "from \n" +
            "`user` u \n" +
            "where user_name in :users \n", nativeQuery = true)
    List<MatchMapping> getMatchUser(@Param("users") List<String> users);
}

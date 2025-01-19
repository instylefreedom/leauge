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
                ", t.tier_name tier \n" +
                ", u.bracket \n" +
                ", u.main_lane \n" +
                ", u.sub_lane \n" +
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
                "left outer join tier t on u.tier = t.tier_id\n" +
                "order by g.winCount*5 - g.loseCount*3 desc, g.matchCount desc", nativeQuery = true)
    List<Map<String,String>> getDashboard(@Param("season") Long season);
}

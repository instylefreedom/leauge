package lol.example.league.repository;

import lol.example.league.entity.User;
import lol.example.league.entity.board.MatchMakingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchMakingRepository extends JpaRepository<MatchMakingEntity, Long> {
}

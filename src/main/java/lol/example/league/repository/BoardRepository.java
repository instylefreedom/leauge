package lol.example.league.repository;

import lol.example.league.entity.GameLog;
import lol.example.league.entity.board.Board;
import lol.example.league.entity.board.BoardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByTypeOrderByCreatedAtDesc(BoardType boardType);
}

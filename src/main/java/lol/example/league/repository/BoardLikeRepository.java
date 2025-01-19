package lol.example.league.repository;

import lol.example.league.entity.board.Board;
import lol.example.league.entity.board.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    void deleteAllByBoardId(Long id);

    @Query(value="select count(c) from BoardLike c where c.boardId = :id")
    Long getLikeCount(@Param("id") Long id);


    Optional<BoardLike> findByBoardIdAndUserId(Long id, Long userId);

    void deleteByBoardIdAndUserId(Long id, Long userId);

    Long countByBoardId(Long id);
}

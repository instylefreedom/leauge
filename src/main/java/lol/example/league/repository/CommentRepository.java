package lol.example.league.repository;

import lol.example.league.entity.board.Board;
import lol.example.league.entity.board.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteByBoardId(Long id);

    Comment findByBoardId(Long id);

    List<Comment> findAllByBoardIdOrderByCreatedAtDesc(Long id);

    Long countByBoardId(Long id);

    List<Comment> findAllByBoardId(Long id);

    void deleteAllByBoardId(Long id);
}

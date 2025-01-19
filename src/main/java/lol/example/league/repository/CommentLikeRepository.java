package lol.example.league.repository;

import lol.example.league.entity.board.Board;
import lol.example.league.entity.board.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    void deleteAllByCommentId(Long commentId);

    @Query(value="select count(c) from CommentLike c where c.commentId = :id")
    Long getLikeCount(@Param("id") Long id);

    Optional<CommentLike> findByCommentIdAndUserId(Long id, Long userId);

    void deleteByCommentIdAndUserId(Long id, Long userId);

    Long countByCommentId(Long id);
}

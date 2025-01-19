package lol.example.league.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lol.example.league.config.auth.dto.SessionUser;
import lol.example.league.dto.request.CommentRequest;
import lol.example.league.dto.response.BoardResponse;
import lol.example.league.dto.response.CommentResponse;
import lol.example.league.dto.response.UserResponse;
import lol.example.league.entity.User;
import lol.example.league.entity.board.Board;
import lol.example.league.entity.board.BoardLike;
import lol.example.league.entity.board.Comment;
import lol.example.league.entity.board.CommentLike;
import lol.example.league.repository.BoardRepository;
import lol.example.league.repository.CommentLikeRepository;
import lol.example.league.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final BoardRepository boardRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserService userService;

    //    =========================== 게시글 목록 ===========================

    // 댓글 전체 조회
    public List<CommentResponse.Body> getCommentData(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("board data not found"));
        List<Comment> comments = repository.findAllByBoardIdOrderByCreatedAtDesc(id);
        Set<Long> userIds = comments.stream().map(c -> c.getUserId()).collect(Collectors.toSet());
        List<User> users = userService.findAllById(userIds);
        Map<Long,User> userMap = users.stream().collect(Collectors.toMap( u -> u.getUserId(), u -> u));

        return comments.stream().map(c -> CommentResponse.Body.builder()
                        .commentId(c.getCommentId())
                        .boardId(c.getBoardId())
                        .parentId(c.getParentId())
                        .userId(c.getUserId())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .createdBy(c.getCreatedBy())
                        .like(getCommentLikeCount(c.getCommentId()))
                        .selfLike(getSelfLike(c.getCommentId(),c.getUserId()))
                        .userName(userMap.get(c.getUserId()).getUserName())
                        .profilePic(userMap.get(c.getUserId()).getPicture())
                        .build()).collect(Collectors.toList());
    }

    public Long getCommentLikeCount(Long id){
        return commentLikeRepository.getLikeCount(id);
    }

    private Long getSelfLike(Long commentId, Long userId) {
        Optional<CommentLike> check = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if(check.isEmpty()){
            return null;
        }
        else{
            return 1L;
        }
    }

    // 댓글 수정
    public String updateCommentData(CommentRequest.body request, SessionUser userInfo) {
        Comment comment = repository.findById(request.getCommentId()).orElseThrow(() -> new EntityNotFoundException("comment data not found"));
        User user =  userService.findAdminByEmail(userInfo.getEmail());
        if(comment.getCreatedBy().equals(user.getUserId())){
            comment.update(user.getUserId(), request.getContent());
            repository.save(comment);
            return "댓글 수정 완료";
        }
        else{
            return "본인 댓글만 수정 할수 있습니다.";
        }
    }

    // 댓글 삭제
    public String deleteCommentData(Long id, SessionUser userInfo) {
        Optional<Comment> comment = repository.findById(id);
        if(ObjectUtils.isEmpty(comment)){
            return "해당 댓글이 존재하지 않습니다";
        }
        User user =  userService.findAdminByEmail(userInfo.getEmail());
        if(comment.get().getCreatedBy().equals(user.getUserId())) {
            repository.deleteById(id);
            commentLikeRepository.deleteAllByCommentId(id);
            return "댓글 삭제 완료";
        }
        else{
            return "본인 댓글만 삭제가 가능합니다";
        }

    }

    // 댓글 좋아요
    @Transactional
    public CommentResponse.Like likeCommentData(Long id, SessionUser userInfo) {
        Optional<Comment> comment = repository.findById(id);
        User user =  userService.findAdminByEmail(userInfo.getEmail());
//        if(ObjectUtils.isEmpty(comment)){
//            return "해당 댓글이 존재하지 않습니다";
//        }
        int stat;
        Optional<CommentLike> check = commentLikeRepository.findByCommentIdAndUserId(id,user.getUserId());
        if(check.isEmpty()){
            CommentLike like = CommentLike.builder()
                    .commentId(id)
                    .userId(user.getUserId())
                    .build();
            commentLikeRepository.save(like);
            stat = 0;
        }
        else{
            commentLikeRepository.deleteByCommentIdAndUserId(id,user.getUserId());
            stat = 1;
        }

        Long count = commentLikeRepository.countByCommentId(id);
        return CommentResponse.Like.builder()
                .stat(stat)
                .like(count)
                .build();

    }

    public String createCommentData(CommentRequest.create request, SessionUser userInfo) {
        User user = userService.findAdminByEmail(userInfo.getEmail());
        Comment comment = Comment.builder()
                .boardId(request.getBoardId())
                .userId(user.getUserId())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .createdBy(user.getUserId())
                .build();
        repository.save(comment);
        return "댓글 저장 완료.";
    }

}

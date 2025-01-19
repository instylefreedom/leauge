package lol.example.league.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lol.example.league.config.auth.dto.SessionUser;
import lol.example.league.dto.request.BoardRequest;
import lol.example.league.dto.response.BoardResponse;
import lol.example.league.dto.response.UserResponse;
import lol.example.league.entity.Role;
import lol.example.league.entity.User;
import lol.example.league.entity.board.Board;
import lol.example.league.entity.board.BoardLike;
import lol.example.league.entity.board.BoardType;
import lol.example.league.entity.board.Comment;
import lol.example.league.repository.BoardLikeRepository;
import lol.example.league.repository.BoardRepository;
import lol.example.league.repository.CommentLikeRepository;
import lol.example.league.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;
    private final CommentRepository commentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserService userService;

    //    =========================== 게시판 목록 ===========================

    // 관리자 게시판 조회
    public List<BoardResponse.Title> getBoardsData(String type) {
        BoardType t = BoardType.valueOf(type);
        List<Board> boards = repository.findAllByTypeOrderByCreatedAtDesc(t);
        AtomicInteger counter = new AtomicInteger(1);
        List<BoardResponse.Title> response = boards.stream().map(b -> BoardResponse.Title.builder()
                .num(counter.getAndIncrement())
                .boardId(b.getBoardId())
                .title(b.getTitle())
                .createdAt(b.getCreatedAt())
                .createdBy(b.getCreatedBy())
                .like(getBoardLikeCount(b.getBoardId()))
                .build()
        ).toList();
        return response;
    }

    public Long getBoardLikeCount(Long id){
        return boardLikeRepository.getLikeCount(id);
    }

    // 게시글 조회
    public BoardResponse.Body getBoardData(Long id) {
        Board board = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("board data not found"));
        Long commentCnt = commentRepository.countByBoardId(id);
        if(board.getCreatedBy() != null) {
            UserResponse user = userService.findById(board.getCreatedBy());
            return BoardResponse.Body.builder()
                    .boardId(board.getBoardId())
                    .title(board.getTitle())
                    .type(board.getType())
                    .content(board.getContent())
                    .createdAt(board.getCreatedAt())
                    .createdBy(board.getCreatedBy())
                    .like(getBoardLikeCount(board.getBoardId()))
                    .selfLike(getSelfLike(board.getBoardId(), user.getUserId()))
                    .userName(user.getUserName())
                    .profilePic(user.getPicture())
                    .commentCnt(commentCnt)
                    .build();
        }
        else{
            return BoardResponse.Body.builder()
                    .boardId(board.getBoardId())
                    .title(board.getTitle())
                    .type(board.getType())
                    .content(board.getContent())
                    .createdAt(board.getCreatedAt())
                    .createdBy(board.getCreatedBy())
                    .like(getBoardLikeCount(board.getBoardId()))
                    .selfLike(0L)
                    .userName("")
                    .profilePic("")
                    .commentCnt(commentCnt)
                    .build();
        }
    }

    private Long getSelfLike(Long boardId, Long userId) {
        Optional<BoardLike> check = boardLikeRepository.findByBoardIdAndUserId(boardId, userId);
        if(check.isEmpty()){
            return null;
        }
        else{
            return 1L;
        }
    }

    // 게시글 작성

    public String createBoardData(BoardRequest.create request, SessionUser userInfo) {
        User user =  userService.findAdminByEmail(userInfo.getEmail());

        if(BoardType.valueOf(request.getType()).equals(BoardType.FREE)){
            Board board = Board.builder()
                    .title(request.getTitle())
                    .userId(user.getUserId())
                    .type(BoardType.valueOf(request.getType()))
                    .content(request.getContent())
                    .createdAt(LocalDateTime.now())
                    .createdBy(user.getUserId())
                    .build();
            repository.save(board);
        }
        if (BoardType.valueOf(request.getType()).equals(BoardType.ADMIN) && user.getRole().equals(Role.ADMIN)){
            Board board = Board.builder()
                    .title(request.getTitle())
                    .userId(user.getUserId())
                    .type(BoardType.valueOf(request.getType()))
                    .content(request.getContent())
                    .createdAt(LocalDateTime.now())
                    .createdBy(user.getUserId())
                    .build();
            repository.save(board);
        }
        else{
            return "관리자 게시판은 관리자만 작성할수 있습니다.";
        }

        return ("게시글 저장 완료");
    }

    // 게시글 삭제
    @Transactional
    public String deleteBoardData(Long id, SessionUser userInfo) {
        Optional<Board> board = repository.findById(id);
        if(ObjectUtils.isEmpty(board)){
            return "해당 게시판이 존재하지 않습니다";
        }
        User user = userService.findAdminByEmail(userInfo.getEmail());
        if(board.get().getCreatedBy().equals(user.getUserId())){
            repository.deleteById(id);
            boardLikeRepository.deleteAllByBoardId(id);
            List<Comment> comments = commentRepository.findAllByBoardId(id);
            if(comments.size()>0){
                commentRepository.deleteAllByBoardId(id);
                for(Comment c : comments){
                    commentLikeRepository.deleteAllByCommentId(c.getCommentId());
                }
            }
            return "게시판이 삭제되었습니다.";
        }
        else{
            return "본인 게시판만 삭제 할수 있습니다.";
        }

    }

    // 게시글 수정
    public String updateBoardData(BoardRequest.update request, SessionUser userInfo) {
        Board board = repository.findById(request.getBoardId()).orElseThrow(() -> new EntityNotFoundException("board data not found"));
        User user =  userService.findAdminByEmail(userInfo.getEmail());
        if(board.getCreatedBy()!= null) {
            if (board.getCreatedBy().equals(user.getUserId())) {
                board.update(request.getTitle(), request.getContent());
                repository.save(board);
                return "게시판 정보 수정 완료";
            } else {
                return "본인 게시판만 수정할수 있습니다";
            }
        }
        else{
            board.updateTemp(request.getTitle(), request.getContent(),user.getUserId(), LocalDateTime.now());
            repository.save(board);
            return "임시 저장글 저장 했습니다.";
        }
    }

    // 게시글 좋아요
    @Transactional
    public BoardResponse.Like likeBoard(Long id, SessionUser userInfo) {
        Optional<Board> board = repository.findById(id);
//        if(ObjectUtils.isEmpty(board)){
//            return "해당 게시판이 존재하지 않습니다";
//        }
        User user =  userService.findAdminByEmail(userInfo.getEmail());
        int stat;
        Optional<BoardLike> check = boardLikeRepository.findByBoardIdAndUserId(id,user.getUserId());
        if(check.isEmpty()){
            BoardLike like = BoardLike.builder()
                    .boardId(id)
                    .userId(user.getUserId())
                    .build();
            boardLikeRepository.save(like);
            stat = 0;
        }
        else{
            boardLikeRepository.deleteByBoardIdAndUserId(id,user.getUserId());
            stat = 1;
        }

        Long count = boardLikeRepository.countByBoardId(id);
        return BoardResponse.Like.builder()
                .stat(stat)
                .like(count)
                .build();
    }

    public String createTempBoardData(BoardRequest.create request) {
        if(BoardType.valueOf(request.getType()).equals(BoardType.FREE)){
            Board board = Board.builder()
                    .title(request.getTitle())
                    .type(BoardType.valueOf(request.getType()))
                    .content(request.getContent())
                    .createdAt(LocalDateTime.now())
                    .build();
            repository.save(board);
        }
        if (BoardType.valueOf(request.getType()).equals(BoardType.ADMIN)){
            Board board = Board.builder()
                    .title(request.getTitle())
                    .type(BoardType.valueOf(request.getType()))
                    .content(request.getContent())
                    .createdAt(LocalDateTime.now())
                    .build();
            repository.save(board);
        }
        else{
            return "관리자 게시판은 관리자만 작성할수 있습니다.";
        }

        return ("게시글 임시 저장 완료");
    }
}

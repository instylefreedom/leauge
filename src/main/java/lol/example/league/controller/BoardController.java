package lol.example.league.controller;

import lol.example.league.config.auth.dto.LoginUser;
import lol.example.league.config.auth.dto.SessionUser;
import lol.example.league.dto.request.BoardRequest;
import lol.example.league.dto.response.BoardResponse;
import lol.example.league.service.BoardService;
import lol.example.league.util.ApiResponse;
import lol.example.league.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService service;

//    =========================== 게시판 목록 ===========================
    // 관리자/자유 게시판 조회
    @GetMapping("/api/boards/data/{type}")
    public ApiResponse getBoardsData(@PathVariable(value="type") String type) {

        List<BoardResponse.Title> response = service.getBoardsData(type);
        return ApiResponseUtil.success(response);
    }

    // 게시글 조회

    @GetMapping("/api/board/data/{id}")
    public ApiResponse getBoardData(@PathVariable(value="id") Long id) {

        BoardResponse.Body response = service.getBoardData(id);
        return ApiResponseUtil.success(response);
    }

    // 게시글 작성
    @PostMapping("/api/board/create")
    public String createBoardData(@RequestBody BoardRequest.create request, @LoginUser SessionUser userInfo) {

        return service.createBoardData(request, userInfo);

    }

    // 게시글 삭제
    @DeleteMapping("/api/board/{id}")
    public String deleteBoardData(@PathVariable(value="id") String id, @LoginUser SessionUser userInfo) {

        return service.deleteBoardData(Long.valueOf(id), userInfo);
    }
    // 게시글 수정

    @PatchMapping("/api/board/update")
    public String deleteBoardData(@RequestBody BoardRequest.update request, @LoginUser SessionUser userInfo) {

        return service.updateBoardData(request, userInfo);
    }

    // 게시글 좋아요
    @PostMapping("/api/board/like/{id}")
    public ApiResponse likeBoard(@PathVariable(value="id") Long id, @LoginUser SessionUser userInfo) {
        //이게 좋아욘지 좋아요 취소인지 구분하던, 나누던
        return ApiResponseUtil.success(service.likeBoard(id, userInfo));
    }

    // 게시글 임시 작성
    @PostMapping("/api/board/temp")
    public String createTempBoardData(@RequestBody BoardRequest.create request) {

        return service.createTempBoardData(request);

    }

}

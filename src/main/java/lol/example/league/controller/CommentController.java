package lol.example.league.controller;

import lol.example.league.config.auth.dto.LoginUser;
import lol.example.league.config.auth.dto.SessionUser;
import lol.example.league.dto.request.CommentRequest;
import lol.example.league.dto.response.BoardResponse;
import lol.example.league.dto.response.CommentResponse;
import lol.example.league.service.CommentService;
import lol.example.league.util.ApiResponse;
import lol.example.league.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    //    =========================== 댓글 ===========================

    // 댓글 전체 조회
    @GetMapping("/api/comment/data/{id}")
    public ApiResponse getCommentData(@PathVariable(value="id") Long id) {

        List<CommentResponse.Body> response = service.getCommentData(id);
        return ApiResponseUtil.success(response);
    }

    // 댓글 작성
    @PostMapping("/api/comment")
    public String getCommentData(@RequestBody CommentRequest.create request, @LoginUser SessionUser userInfo) {

        return service.createCommentData(request, userInfo);

    }


    // 댓글 수정
    @PatchMapping("/api/comment")
    public String getCommentData(@RequestBody CommentRequest.body request, @LoginUser SessionUser userInfo) {

        return service.updateCommentData(request, userInfo);

    }

    // 댓글 삭제
    @DeleteMapping("/api/comment/{id}")
    public String deleteCommentData(@PathVariable(value="id") String id, @LoginUser SessionUser userInfo) {

        return service.deleteCommentData(Long.valueOf(id), userInfo);

    }

    // 댓글 좋아요
    @PostMapping("/api/comment/like/{id}")
    public ApiResponse likeCommentData(@PathVariable(value="id") String id, @LoginUser SessionUser userInfo) {

        return ApiResponseUtil.success(service.likeCommentData(Long.valueOf(id), userInfo));

    }
}

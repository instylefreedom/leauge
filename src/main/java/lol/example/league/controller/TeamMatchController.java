package lol.example.league.controller;

import lol.example.league.dto.request.UserMatchRequest;
import lol.example.league.dto.response.MatchResponse;
import lol.example.league.dto.response.UserMatchResponse;
import lol.example.league.service.TeamMatchService;
import lol.example.league.service.UserService;
import lol.example.league.util.ApiResponse;
import lol.example.league.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamMatchController {

    @Autowired
    private TeamMatchService teamMatchService;

    /**
     * 팀 매칭 작업. 결과 저장
     * @return
     */
    @PostMapping ("/api/team/match")
    public ApiResponse match(@RequestBody UserMatchRequest request) throws Exception {

        teamMatchService.matchUser(request.getUsers());
        return ApiResponseUtil.success();

    }

}

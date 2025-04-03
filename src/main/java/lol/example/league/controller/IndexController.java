package lol.example.league.controller;

import jakarta.servlet.http.HttpSession;
import lol.example.league.config.auth.dto.LoginUser;
import lol.example.league.config.auth.dto.SessionUser;
import lol.example.league.dto.response.*;
import lol.example.league.entity.Role;
import lol.example.league.entity.User;
import lol.example.league.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserService userService;
    private final GameService gameService;
    private final HttpSession httpSession;
    private final BoardService boardService;
    private final CommentService commentService;
    private final TeamMatchService teamMatchService;


// ========================= 내전 통계 조회 ========================
    /**
     * 로그인 전 초기 화면 + 로그인 후 내전 기록 대시보드 화면
     * @param model
     * @param user
     * @return
     */
    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user){
//        model.addAttribute("posts",userService.getWebDashboard());
//        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if(user != null){
//            model.addAttribute("posts",userService.getWebDashboard());
//            Long id = userService.findByEmail(user.getEmail());
//            model.addAttribute("userName", user.getName());
//            model.addAttribute("user", id);
//            return "dashboard";
//            추후 홈화면 변경
//            if(user != null){
            List<BoardResponse.Title> board = boardService.getBoardsData("ADMIN");
            model.addAttribute("board",board);
            Long id = userService.findByEmail(user.getEmail());
            model.addAttribute("userName", user.getName());
            model.addAttribute("user", id);
//            }
            return "board/admin-board";
        }
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, @LoginUser SessionUser user){
//        model.addAttribute("posts",userService.getWebDashboard());
//        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        if(user != null){
//            model.addAttribute("posts",userService.getWebDashboard());
            Long id = userService.findByEmail(user.getEmail());
            model.addAttribute("userName", user.getName());
            model.addAttribute("user", id);
            return "dashboard";
        }
        return "index";
    }


// ========================= 내 정보 조회 ========================
    /**
     * 대시보드에서 테이블 누를때 관리자가 모든 인원 정보 조회 및 수정 권한을 위한 화면
     * @param userName
     * @param model
     * @param user
     * @return
     */
    @GetMapping("/user/admin/update/{userName}")
    public String userAdminUpdate(@PathVariable(value="userName") String userName, Model model, @LoginUser SessionUser user){
        User admin = userService.findAdminByEmail(user.getEmail());
        if(admin.getRole().equals(Role.ADMIN)){
            UserResponse data = userService.findUserResponseByName(userName);
            model.addAttribute("user",data);

            return "user/admin-update";
        }
//        Long id = userService.findByEmail(user.getEmail());
        model.addAttribute("userName", user.getName());
        model.addAttribute("user", admin.getUserId());
        return "dashboard";

    }

    /**
     * 상단 메뉴의 내 정보 조회 및 수정 화면
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/user/update/{userId}")
    public String userUpdate(@PathVariable(value="userId") Long id, Model model){
        UserResponse data = userService.findById(id);
        model.addAttribute("user",data);

        return "user/user-update";

    }

// ========================= 내전 기록 조회 ========================
    /**
     * 내전 기록 저장 화면
     * @return
     */
    @GetMapping("/log/save")
    public String logSave(){
        return "game/log-save";
    }

    /**
     * 내전 전적 조회 화면
     * @param model
     * @param user
     * @return
     */
    @GetMapping("/game/view")
    public String viewGame(Model model, @LoginUser SessionUser user){
        if(user != null){
            List<Map<String,Object>> data = gameService.getGameData();
            model.addAttribute("games",data);
            Long id = userService.findByEmail(user.getEmail());
            model.addAttribute("userName", user.getName());
            model.addAttribute("user", id);
        }
        return "game/game-view";

    }

    /**
     * 특정 게임 조회 화면
     * @param model
     * @param user
     * @param round
     * @param play_date
     * @return
     */
    @GetMapping("/game/view/detail")
    public String viewGame(Model model, @LoginUser SessionUser user, @RequestParam Integer round, Integer play_date){
        if(user != null){
            GameResponse data = gameService.getGameDataDetail(round,play_date);
            model.addAttribute("game",data);
            Long id = userService.findByEmail(user.getEmail());
            model.addAttribute("userName", user.getName());
            model.addAttribute("user", id);
        }
        return "game/game-view-detail";

    }


// ========================= 게시판 조회 ========================

    @GetMapping("/board/admin")
    public String viewAdminBoard(Model model, @LoginUser SessionUser user){
        if(user != null){
            List<BoardResponse.Title> board = boardService.getBoardsData("ADMIN");
            model.addAttribute("board",board);
            Long id = userService.findByEmail(user.getEmail());
            model.addAttribute("userName", user.getName());
            model.addAttribute("user", id);
        }
        return "board/admin-board";
    }

    @GetMapping("/board/admin/create")
    public String createAdminBoard(Model model, @LoginUser SessionUser user){
        User admin = userService.findAdminByEmail(user.getEmail());
        if(admin.getRole().equals(Role.ADMIN)){
            return "board/board-create";
        }
        model.addAttribute("userName", user.getName());
        model.addAttribute("user", admin.getUserId());
        return "board/admin-board";

    }

    @GetMapping("/board/edit/{boardId}")
    public String editAdminBoard(Model model, @LoginUser SessionUser user,@PathVariable(value="boardId") String boardId){
//        User admin = userService.findAdminByEmail(user.getEmail());
//        if(admin.getRole().equals(Role.ADMIN)){
//            return "board/board-create";
//        }
        BoardResponse.Body boardData = boardService.getBoardData(Long.valueOf(boardId));
        model.addAttribute("board", boardData);
//        model.addAttribute("user", admin.getUserId());

        return "board/board-update";

    }

    @GetMapping("/board/data")
    public String getBoard(Model model, @LoginUser SessionUser user,@RequestParam Long id){
//        User admin = userService.findAdminByEmail(user.getEmail());
//        if(admin.getRole().equals(Role.ADMIN)){
//            return "board-create";
//        }
        BoardResponse.Body boardData = boardService.getBoardData(id);
        List<CommentResponse.Body> commentData = commentService.getCommentData(id);
        User admin = userService.findAdminByEmail(user.getEmail());
        model.addAttribute("userName", user.getName());
        model.addAttribute("user", admin.getUserId());
        model.addAttribute("board", boardData);
        model.addAttribute("comment", commentData);
        return "board/board-detail";

    }



// ========================= 매치 메이킹 ========================
    @GetMapping("/match/start")
    public String matchStart() {
        return "match/match-start";
    }

    @GetMapping("/match/result")
    public String matchResult(Model model) {

        String rule = teamMatchService.getRule();
        UserMatchResponse response = teamMatchService.getMatchResult();
        model.addAttribute("rule",rule);
        model.addAttribute("team1Score",response.getTeam1Total());
        model.addAttribute("team2Score",response.getTeam2Total());
        model.addAttribute("team1",response.getTeam1());
        model.addAttribute("team2",response.getTeam2());

        return "match/match-result";
    }


//    @GetMapping("/test/liver")
//    public void livertest(Model model) {
//
//        teamMatchService.liverTest();
//
//    }



}

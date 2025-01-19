package lol.example.league.controller;

import lol.example.league.config.auth.dto.LoginUser;
import lol.example.league.config.auth.dto.SessionUser;
import lol.example.league.dto.MatchResult;
import lol.example.league.dto.request.LogRequest;
import lol.example.league.dto.response.MatchResponse;
import lol.example.league.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;



    /**
     * 로그 정보 전송   (사용 하지 않는 상태)
     * @param roundId
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @GetMapping("/api/log/get/data")
    public List<MatchResponse> getUserData(@RequestParam (required = false, value = "userId") Long roundId ) {

        return logService.getLogData(Math.toIntExact(roundId));
    }

    /**
     *  신규 로그 정보 저장
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @PostMapping("/api/log/save/data")
    public String saveUserData(@RequestBody LogRequest request, @LoginUser SessionUser userInfo) throws Exception {

        return logService.saveLogData(request,userInfo);
    }

    /**
     *  최근 전적 삭제 작업
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @DeleteMapping("/api/log/delete/latest")
    public String saveUserData(@LoginUser SessionUser userInfo) throws Exception {

        return logService.deleteLogLatest(userInfo);
    }

//    /**
//     * 로그 수정?
//     * @param id
//     * @param user
//     * @return
//     */
//    @PutMapping("/api/log/update/{id}")
//    public Long updateUserData(@PathVariable(value="id") Long id, @RequestBody UserRequest user){
//        return userService.updateWebUserData(id, user);
//    }
//
//    /**
//     * 로그 삭제?
//     * @param id
//     * @return
//     */
//    @DeleteMapping("/api/log/delete/{id}")
//    public Long deleteUserData(@PathVariable(value="id") Long id){
//        userService.deleteWebUserData(id);
//        return id;
//    }
    
    
    
    
    
    

//    //구글 시트에서 받은 로그 정보 저장
//    @GetMapping("save/log/data")
//    public String saveLogData() throws GeneralSecurityException, IOException{
//        return logService.saveLogData();
//    }

//    //구글 시트에 보낼 로그 정보 전달
//    @GetMapping("get/log/data")
//    public String getLogData(@RequestParam Integer date, Integer round, Integer set) throws GeneralSecurityException, IOException{
//        logService.getLogData(date,round,set);
//    }
//
//    //구글 시트에 특정 기록 수정
//    @GetMapping("update/log/data")
//    public String updateLogData(@RequestParam Integer logId) throws GeneralSecurityException, IOException{
//        logService.updateLogData(logId);
//    }
//
//    //구글 시트에 특정 기록 삭제
//    @GetMapping("delete/log/data")
//    public String deleteLogData() throws GeneralSecurityException, IOException{
//        logService.deleteLogData();
//    }

}








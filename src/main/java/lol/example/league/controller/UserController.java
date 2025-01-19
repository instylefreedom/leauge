package lol.example.league.controller;

import lol.example.league.config.auth.dto.LoginUser;
import lol.example.league.config.auth.dto.SessionUser;
import lol.example.league.dto.GoogleSheetDTO;
import lol.example.league.dto.GoogleSheetResponseDTO;
import lol.example.league.dto.request.UserRequest;
import lol.example.league.dto.response.UserResponse;
import lol.example.league.service.UserService;
import lol.example.league.util.ApiResponse;
import lol.example.league.util.ApiResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

//    /**
//     * 유저 정보 전송  (사용 하지 않음)
//     * @param userId
//     * @return
//     */
//    @GetMapping("/api/user/get/data")
//    public List<UserResponse> getUserData(@RequestParam (required = false, value = "userId") Long userId ) {
//
////        return userService.getUserData(userId);
//        return userService.getWebUserData(userId);
//    }
//
//    /**
//     *  신규 유저 정보 저장
//     * @return
//     */
//    @PostMapping("/api/user/save/data")
//    public String saveUserData(@RequestBody UserRequest request)  {
////        return userService.saveUserData();
//        return userService.saveWebUserData(request);
//    }

    /**
     * 유저 정보 수정
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/api/user/update/{id}")
    public String updateUserData(@PathVariable(value="id") Long id, @RequestBody UserRequest user, @LoginUser SessionUser userInfo){
        return userService.updateWebUserData(id, user, userInfo);
    }

    /**
     * 유저 정보 삭제
     * @param id
     * @return
     */
    @DeleteMapping("/api/user/delete/{id}")
    public Long deleteUserData(@PathVariable(value="id") Long id, @LoginUser SessionUser userInfo){
        userService.deleteWebUserData(id,userInfo);
        return id;
    }

    /**
     * 유저 전체 이름 get
     * @return
     */
    @GetMapping("/api/user/get/name")
    public ApiResponse getUserName() {

        return userService.getWebUserName();
    }

    /**
     * 대시보드 databable 정보 전송
     * @param userInfo
     * @return
     */
    @PostMapping("/api/user/get/dashboard/{season}")
    public ApiResponse getUserDashboard(@LoginUser SessionUser userInfo, @PathVariable(value="season") Long season) {
        if(userInfo != null) {
            return userService.getWebDashboard(season);
        }
        return ApiResponseUtil.success();
    }




//    --------------------- Goggle Sheet API ---------------------

//    /**
//     * 구글 시트에 전체/특정 유저 정보 전송
//     * @param userId
//     * @return
//     * @throws GeneralSecurityException
//     * @throws IOException
//     */
//    @GetMapping("/get/user/data")
//    public List<UserResponse> getUserData(@RequestParam (required = false, value = "userId") Long userId ) throws GeneralSecurityException, IOException {
//
////        return userService.getUserData(userId);
//        return userService.getWebUserData(userId);
//    }

//    /**
//     * 구글 시트에 변경한 유저 정보 업데이트
//     * @return
//     * @throws GeneralSecurityException
//     * @throws IOException
//     */
//    @GetMapping("update/user/data")
//    public String updateUserData() throws GeneralSecurityException, IOException {
//        return userService.updateUserData();
//    }

//    /**
//     * 구글 시트로 부터 신규 유저 정보 저장
//     * @return
//     * @throws GeneralSecurityException
//     * @throws IOException
//     */
//    @GetMapping("save/user/data")
//    public String saveUserData() throws GeneralSecurityException, IOException {
//        return userService.saveUserData();
//    }

    /**
     * 구글 시트에서 받은 유저 id 로 유저 정보 삭제
     * @param userId
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @GetMapping("delete/user/data")
    public String deleteUserData(@RequestParam (value = "userId") long userId ) throws GeneralSecurityException, IOException {
        return userService.deleteUserData(userId);
    }

}

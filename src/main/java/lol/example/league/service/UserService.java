package lol.example.league.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lol.example.league.config.auth.dto.SessionUser;
import lol.example.league.dto.GoogleSheetDTO;
import lol.example.league.dto.GoogleSheetResponseDTO;
import lol.example.league.dto.request.UserRequest;
import lol.example.league.dto.response.UserNameResponse;
import lol.example.league.dto.response.UserResponse;
import lol.example.league.entity.GameLog;
import lol.example.league.entity.GameResult;
import lol.example.league.entity.Role;
import lol.example.league.entity.User;
import lol.example.league.repository.GameLogRepository;
import lol.example.league.repository.UserRepository;
import lol.example.league.util.ApiResponse;
import lol.example.league.util.ApiResponseUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

//    @Autowired
//    GoogleApiUtil googleApiUtil;

    private final UserRepository userRepository;
    private final GameLogRepository logRepository;

//    public String getUserData(long user_id) throws GeneralSecurityException, IOException {
//        return googleApiUtil.getUserDataToSheet(user_id);
//    }
//
//    public String updateUserData() throws GeneralSecurityException, IOException {
//        return googleApiUtil.updateUserData();
//    }
//
//    public String saveUserData() throws GeneralSecurityException, IOException {
//        return googleApiUtil.saveUserData();
//    }

    public String deleteUserData(long userId) throws GeneralSecurityException, IOException {
//        return googleApiUtil.deleteUserData();
        userRepository.deleteById(userId);
        return null;
    }

//    public List<UserResponse> getWebUserData(Long userId) {
//        List<UserResponse> response = new ArrayList<>();
//        if (userId == 0){
//            List<User> user = userRepository.findAll();
//            response = user.stream().map(
//                    data ->  UserResponse.builder()
//                            .userId(data.getUserId())
//                            .userName(data.getUserName())
//                            .tier(data.getTier())
//                            .mainLane(data.getMainLane())
//                            .subLane(data.getSubLane())
//                            .build()
//            ).toList();
//        }
//        else if (userId != 0){
//            User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found"));
//            UserResponse data = UserResponse.builder()
//                    .userId(user.getUserId())
//                    .userName(user.getUserName())
//                    .tier(user.getTier())
//                    .mainLane(user.getMainLane())
//                    .subLane(user.getSubLane())
//                    .build();
//            response.add(data);
//
//        }
//        return response;
//    }
//
//
//    public String saveWebUserData(UserRequest request) {
//
//        Optional<User> user = userRepository.findByEmail(request.getUserEmail());
//        if(ObjectUtils.isEmpty(user)){
//            return "이메일 정보가 없습니다. 구글로그인에 사용하신 메일을 작성해 주세요";
//        }
//        user.map(
//        u -> u.setUserName(request.getUserName())
//                .setTier(request.getTier())
//                .setMainLane(request.getMainLane())
//                .setSubLane(request.getSubLane())
//        ).map(userRepository::save);
////        User user = User.builder()
////                .userName(request.getUserName())
////                .tier(request.getTier())
////                .mainLane(request.getMainLane())
////                .subLane(request.getSubLane())
//////                .createdAt(LocalDateTime.now())
////                .build();
////        userRepository.save(user);
//        return "유저 정보가 구글 메일과 연동되었습니다.";
//    }

    @Transactional
    public String updateWebUserData(Long id, UserRequest request, SessionUser userInfo){
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("user not found"));
        User admin =  userRepository.findByEmail(userInfo.getEmail()).orElseThrow(() -> new EntityNotFoundException("admin not found"));

        if(userInfo.getEmail().equals(user.getEmail()) || admin.getRole().equals(Role.ADMIN)) {
            user.update(request.getUserName(), request.getMainLane(), request.getSubLane(), admin.getUserId(), LocalDateTime.now());
        }
        if(admin.getRole().equals(Role.ADMIN)) {
            user.updateRating(request.getRating());
        }
        return "유저 정보가 저장되었습니다";
    }

    @Transactional
    public ApiResponse getWebDashboard(Long season) {
//        return userRepository.findAll().stream()
//                .map(UserResponse::new)
//                .collect(Collectors.toList());
        List<UserResponse> response = new ArrayList<>();
        List<Map<String,String>> dashboardData = userRepository.getDashboard(season);
        String rate = "";
        String tier = "";
        int cwin = 0;
        int close = 0;
        for(Map<String,String> d: dashboardData){
            if(!String.valueOf(d.get("rate")).equals("null")){
                rate = (String.valueOf(d.get("rate"))).substring(0,4) + "%";
            }
            else{
                rate = "";
            }

            if (d.get("tier") != null && d.get("bracket") != null){
                tier = d.get("tier") + String.valueOf(d.get("bracket"));
            }
            else if(d.get("tier") != null ){
                tier = d.get("tier");
            }
            else{
                tier = "";
            }

//            GameLog last = logRepository.findTop1ByUserIdAndSeasonOrderByGameIdDesc(Long.valueOf(String.valueOf(d.get("user_id"))),season);
//            List<GameLog> gameList = logRepository.findByUserIdAndSeasonOrderByGameIdDesc(Long.valueOf(String.valueOf(d.get("user_id"))),season);

//            List<Map<String,String>> roundResult = logRepository.getConsecutiveRound(Long.valueOf(String.valueOf(d.get("user_id"))),season);
//            if (roundResult.size() > 0) {
//                String result = roundResult.get(0).get("result");
//                for(Map<String,String> round : roundResult){
//                    if(round.get("result").equals(result)){
//                        if(round.get("result").equals(GameResult.LOSE.toString())){
//                            close+=1;
//                        }else if(round.get("result").equals(GameResult.WIN.toString())){
//                            cwin+=1;
//                        }
//                    }
//                    else{
//                        break;
//                    }
//                }
//            }



            UserResponse data = UserResponse.builder()
                    .userId(Long.valueOf(String.valueOf(d.get("user_id"))))
                    .userName(d.get("user_name"))
                    .tier(tier)
                    .mainLane(d.get("main_lane"))
                    .subLane(d.get("sub_lane"))
                    .matchNumber(Integer.valueOf(String.valueOf(d.get("matchCount"))))
                    .win(Integer.valueOf(String.valueOf(d.get("winCount"))))
                    .loss(Integer.valueOf(String.valueOf(d.get("loseCount"))))
                    .winRate(rate)
                    .winPoint(Integer.valueOf(String.valueOf(d.get("point"))))
//                    .cwin(cwin)
//                    .close(close)
                    .build();
            response.add(data);
//            cwin=0;
//            close=0;
        }
        return ApiResponseUtil.success(response);
//        return response;
    }

    @Transactional
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("no user data"));
        return new UserResponse(user);
    }

    @Transactional
    public UserResponse findUserResponseByName(String name) {
        User user = userRepository.findByUserName(name);
        return new UserResponse(user);
    }

    @Transactional
    public void deleteWebUserData(Long id,SessionUser userInfo) {
        User admin = userRepository.findByEmail(userInfo.getEmail()).orElseThrow(() -> new EntityNotFoundException("no user data"));
        if (admin.getRole()== Role.ADMIN) {
            User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("no user data"));
            userRepository.delete(user);
        }
    }

    public User findByName(String name){
        return userRepository.findByUserName(name);
    }

    public ApiResponse getWebUserName() {
        List<String> name = userRepository.getAllUserName();
        List<UserNameResponse> response = new ArrayList<>();
        for(String d: name){
            UserNameResponse user = new UserNameResponse(d);
            response.add(user);
        }
        return ApiResponseUtil.success(response);
    }

    public Long findByEmail(String email) {

        Optional<User> user = userRepository.findByEmail(email);
            return user.get().getUserId();
    }

    public User findAdminByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("no user data"));

    }

    public List<User> findAllById(Set<Long> userIds) {
        return userRepository.findAllById(userIds);
    }

    public Optional<User> findOptionalById(Long userId) {
        return userRepository.findById(userId);
    }
}

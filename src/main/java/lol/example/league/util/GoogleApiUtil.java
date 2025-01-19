//package lol.example.league.util;
//
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.Permission;
//import com.google.api.services.sheets.v4.Sheets;
//import com.google.api.services.sheets.v4.SheetsScopes;
//import com.google.api.services.sheets.v4.model.*;
//import lol.example.league.dto.GoogleSheetDTO;
//import lol.example.league.dto.GoogleSheetResponseDTO;
//import lol.example.league.entity.GameLog;
//import lol.example.league.entity.GameResult;
//import lol.example.league.entity.User;
//import lol.example.league.repository.GameLogRepository;
//import lol.example.league.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.security.GeneralSecurityException;
//import java.util.*;
//
//@Component
//public class GoogleApiUtil {
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    GameLogRepository gameLogRepository;
//
//    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
//    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
//    private static final String TOKENS_DIRECTORY_PATH = "tokens/path";
//
//    private static final String SPREADSHEET_ID = "105ljgYBw_QATlefRk0T605_Sq-S7grNtUz5tbAemHic";
//
//    /**
//     * Global instance of the scopes required by this quickstart.
//     * If modifying these scopes, delete your previously saved tokens/ folder.
//     */
//    private static final List<String> SCOPES =
//            Arrays.asList(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE);
//    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
//
//    /**
//     * Creates an authorized Credential object.
//     *
//     * @param HTTP_TRANSPORT The network HTTP Transport.
//     * @return An authorized Credential object.
//     * @throws IOException If the credentials.json file cannot be found.
//     */
//    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
//            throws IOException {
//        // Load client secrets.
//        InputStream in = GoogleApiUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//        if (in == null) {
//            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
//        }
//        GoogleClientSecrets clientSecrets =
//                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(System.getProperty("user.home") ,TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
//        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//    }
//
//    private static Sheets getSheetService() throws GeneralSecurityException, IOException {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        Sheets service =
//                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                        .setApplicationName(APPLICATION_NAME)
//                        .build();
//        return service;
//    }
//
//    private static Drive getDriveService() throws GeneralSecurityException, IOException {
//        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//        Drive service =
//                new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
//                        .setApplicationName(APPLICATION_NAME)
//                        .build();
//        return service;
//    }
//
//
//    public String getUserDataToSheet(Long userId) throws GeneralSecurityException, IOException {
//        Sheets service = getSheetService();
//        List<List<Object>> data = new ArrayList<>();
//        if(userId == 0){
//            List<User> user = userRepository.findAll();
//            int cnt = 0;
//            for(User info: user){
//                List userInfo = new ArrayList<>();
//                userInfo.add(info.getUserId());
//                userInfo.add(info.getUserName());
//                userInfo.add(info.getTier());
//                userInfo.add(info.getMainLane());
//                userInfo.add(info.getSubLane());
//                data.add(cnt,userInfo);
//                cnt+=1;
//            }
//        }
//        else {
//            Optional<User> user = userRepository.findById(userId);
//            if(user.isPresent()){
//                List userInfo = new ArrayList<>();
//                userInfo.add(user.get().getUserId());
//                userInfo.add(user.get().getUserName());
//                userInfo.add(user.get().getTier());
//                userInfo.add(user.get().getMainLane());
//                userInfo.add(user.get().getSubLane());
//                data.add(0,userInfo);
//            }
//        }
//
//
//        ValueRange valueRange = new ValueRange().setValues(data);
//        if(userId == 0) {
//            service.spreadsheets().values().update(SPREADSHEET_ID, "useredit!B5", valueRange).setValueInputOption("RAW").execute();
//        }
//        else{
//            service.spreadsheets().values().update(SPREADSHEET_ID, "useredit!I5", valueRange).setValueInputOption("RAW").execute();
//        }
//        //response 객체 생성
//        GoogleSheetResponseDTO response = new GoogleSheetResponseDTO();
//        response.setSpreadSheetId(SPREADSHEET_ID);
////        response.setSpreadSheetUrl(createdResponse.getSpreadsheetUrl());
//        return null;
//    }
//
//    public String updateUserData() throws GeneralSecurityException, IOException {
//        Sheets service = getSheetService();
//        final String range = "useredit!I5:M5";
//
//        ValueRange response = service.spreadsheets().values()
//                .get(SPREADSHEET_ID, range)
//                .execute();
//
//        List<List<Object>> values = response.getValues();
//
//                List<Object> row = values.get(0);
//                User userInfo = new User();
//                userInfo.setUserId(Long.valueOf((String) row.get(0)));
//                userInfo.setTier(String.valueOf(row.get(1)));
//                userInfo.setMainLane(String.valueOf(row.get(2)));
//                userInfo.setSubLane(String.valueOf(row.get(3)));
//
//                Optional<User> userUpdate = userRepository.findById(Long.valueOf((String) row.get(0)))
//                        .map(user -> user.setUserName((String) row.get(1)))
//                        .map(user -> user.setTier((String) row.get(2)))
//                        .map(user -> user.setMainLane((String) row.get(3)))
//                        .map(user -> user.setSubLane((String) row.get(4)))
//                        .map(user -> userRepository.save(user));
//
//        return null;
//    }
//
//    public String saveUserData() throws GeneralSecurityException, IOException {
//        Sheets service = getSheetService();
//        final String range = "useredit!P5:S5";
//        ValueRange response = service.spreadsheets().values()
//                .get(SPREADSHEET_ID, range)
//                .execute();
//
//        List<List<Object>> values = response.getValues();
//        List<Object> row = values.get(0);
//        User user = User.builder()
//                .userName(String.valueOf(row.get(0)))
//                .tier(String.valueOf(row.get(1)))
//                .mainLane(String.valueOf(row.get(2)))
//                .subLane(String.valueOf(row.get(3)))
//                .build();
//        userRepository.save(user);
//
//        return null;
//    }
//
////    public String saveLogData() throws GeneralSecurityException, IOException{
////        Sheets service = getSheetService();
////        final String range = "history!C10:C19";
////        ValueRange response = service.spreadsheets().values()
////                .get(SPREADSHEET_ID, range)
////                .execute();
////        List<List<Object>> values = response.getValues();
////        final String range2 = "history!C4:C8";
////        ValueRange response2 = service.spreadsheets().values()
////                .get(SPREADSHEET_ID, range)
////                .execute();
////        List<List<Object>> values2 = response.getValues();
////        List<Object> row2 = values2.get(0);
////        List<Object> row;
////        GameResult team1 = null;
////        GameResult team2 = null;
////        Integer winTeam = Integer.valueOf((String) row2.get(4));
////        if(winTeam == 1){
////            team1 = GameResult.WIN;
////            team2 = GameResult.LOSE;
////        }
////        else if(winTeam == 2) {
////            team1 = GameResult.LOSE;
////            team2 = GameResult.WIN;
////        }
////
////        for (int i = 0 ; i < 5; i ++ ){
////            row = values.get(i);
////            GameLog gamelog = GameLog.builder()
////                    .playDate(Integer.valueOf((String) row2.get(1)))
////                    .season(Integer.valueOf((String) row2.get(0)))
////                    .round(Integer.valueOf((String) row2.get(2)))
////                    .set(Integer.valueOf((String) row2.get(3)))
////                    .result(team1)
////                    .team(1)
////                    .playerName(String.valueOf(row.get(0)))
////                    .build();
////            gameLogRepository.save(gamelog);
////        }
////        for (int i = 5 ; i < 10; i ++ ){
////            row = values.get(i);
////            GameLog gamelog = GameLog.builder()
////                    .playDate(Integer.valueOf((String) row2.get(1)))
////                    .season(Integer.valueOf((String) row2.get(0)))
////                    .round(Integer.valueOf((String) row2.get(2)))
////                    .set(Integer.valueOf((String) row2.get(3)))
////                    .result(team2)
////                    .team(2)
////                    .playerName(String.valueOf(row.get(0)))
////                    .build();
////            gameLogRepository.save(gamelog);
////        }
////
////        return null;
////    }
//}
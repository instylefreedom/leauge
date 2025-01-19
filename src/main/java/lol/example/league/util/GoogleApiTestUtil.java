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
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.security.GeneralSecurityException;
//import java.util.*;
//
//public class GoogleApiTestUtil {
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
//    /**
//     * Prints the names and majors of students in a sample spreadsheet:
//     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
//     */
////    public static void main(String... args) throws IOException, GeneralSecurityException {
////        // Build a new authorized API client service.
////        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
////        final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
////        final String range = "Class Data!A2:E";
////        Sheets service =
////                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
////                        .setApplicationName(APPLICATION_NAME)
////                        .build();
////        ValueRange response = service.spreadsheets().values()
////                .get(spreadsheetId, range)
////                .execute();
////        List<List<Object>> values = response.getValues();
////        if (values == null || values.isEmpty()) {
////            System.out.println("No data found.");
////        } else {
////            System.out.println("Name, Major");
////            for (List row : values) {
////                // Print columns A and E, which correspond to indices 0 and 4.
////                System.out.printf("%s, %s\n", row.get(0), row.get(4));
////            }
////        }
////    }
//
//
//    public Map<Object, Object> getDataFromSheet() throws IOException, GeneralSecurityException {
//        Sheets service = getSheetService();
//        final String spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms";
//        final String range = "Class Data!A2:E";
//
//        ValueRange response = service.spreadsheets().values()
//                .get(spreadsheetId, range)
//                .execute();
//
//        List<List<Object>> values = response.getValues();
//        Map<Object, Object> googleSheetData = new HashMap<>();
//
//        if (values == null || values.isEmpty()) {
//            System.out.println("No data found.");
//        } else {
//            System.out.println("Name, Major");
//            for (List row : values) {
//                // Print columns A and E, which correspond to indices 0 and 4.
//                googleSheetData.put(row.get(0),row.get(4));
//                System.out.printf("%s, %s\n", row.get(0), row.get(4));
//            }
//        }
//        return googleSheetData;
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
//    public String createGoogleSheet(GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        Sheets service = getSheetService();
//        // 전체 파일 설정
//        SpreadsheetProperties spreadsheetProperties = new SpreadsheetProperties();
//        spreadsheetProperties.setTitle(request.getSheetName());
//        // 파일 내 시트 설정
//        SheetProperties sheetProperties = new SheetProperties();
//        sheetProperties.setTitle(request.getSheetName());
//
//        // 시트 및 파일 객체 생성
//        Sheet sheet = new Sheet().setProperties(sheetProperties);
//        Spreadsheet spreadsheet = new Spreadsheet().setProperties(spreadsheetProperties)
//                .setSheets(Collections.singletonList(sheet));
//        // 구글 시트 생성 및 url 반
//        return service.spreadsheets().create(spreadsheet).execute().getSpreadsheetUrl();
//
//    }
//
//    public GoogleSheetResponseDTO updateGoogleSheet(GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        Sheets service = getSheetService();
////        // 전체 파일 설정
////        SpreadsheetProperties spreadsheetProperties = new SpreadsheetProperties();
////        spreadsheetProperties.setTitle(request.getSheetName());
////        // 파일 내 시트 설정
////        SheetProperties sheetProperties = new SheetProperties();
////        sheetProperties.setTitle(request.getSheetName());
////
////        // 시트 및 파일 객체 생성
////        Sheet sheet = new Sheet().setProperties(sheetProperties);
////        Spreadsheet spreadsheet = new Spreadsheet().setProperties(spreadsheetProperties)
////                .setSheets(Collections.singletonList(sheet));
////
////        Spreadsheet createdResponse = service.spreadsheets().create(spreadsheet).execute();
//
//        // update 값 준비 및 실행
//        ValueRange valueRange = new ValueRange().setValues(request.getDataToBeUpdated());
//        service.spreadsheets().values().update(SPREADSHEET_ID,"A1",valueRange).setValueInputOption("RAW").execute();
//
//        //response 객체 생성
//        GoogleSheetResponseDTO response = new GoogleSheetResponseDTO();
//        response.setSpreadSheetId(SPREADSHEET_ID);
////        response.setSpreadSheetUrl(createdResponse.getSpreadsheetUrl());
//        return response;
//    }
//
//    public GoogleSheetResponseDTO updateSheetAndSendEmail(GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        Sheets service = getSheetService();
//
//        // update 값 준비 및 실행
//        ValueRange valueRange = new ValueRange().setValues(request.getDataToBeUpdated());
//        service.spreadsheets().values().update(request.getSheetId(),"A1",valueRange).setValueInputOption("RAW").execute();
//
//        //response 객체 생성
//        GoogleSheetResponseDTO response = new GoogleSheetResponseDTO();
//        response.setSpreadSheetId(request.getSheetId());
//
//
//        // to assign permission and email notification
//        Drive driveService = getDriveService();
//
//        if(!request.getEmails().isEmpty()) {
//            request.getEmails().forEach(emailAddress -> {
//                Permission permission = new Permission().setType("user").setRole("writer").setEmailAddress(emailAddress);
//                try {
//                    driveService.permissions().create(request.getSheetId(), permission).setSendNotificationEmail(true).setEmailMessage("구글 시트 권한 이메일 발송 테스트 진행").execute();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        }
//        return response;
//    }
//}

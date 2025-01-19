//package lol.example.league.service;
//
//import lol.example.league.dto.GoogleSheetDTO;
//import lol.example.league.dto.GoogleSheetResponseDTO;
//import lol.example.league.util.GoogleApiTestUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class TestService {
//
//    GoogleApiTestUtil googleApiUtil;
//
//    public Map<Object, Object> getDataFromGoogleSheet() throws GeneralSecurityException, IOException {
//        return googleApiUtil.getDataFromSheet();
//    }
//
//    public String createSheet(GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        return googleApiUtil.createGoogleSheet(request);
//    }
//
//    public GoogleSheetResponseDTO updateSheet(GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        return googleApiUtil.updateGoogleSheet(request);
//    }
//
//    public GoogleSheetResponseDTO updateSheetAndSendEmail(GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        return googleApiUtil.updateSheetAndSendEmail(request);
//    }
//}

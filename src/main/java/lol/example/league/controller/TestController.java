//package lol.example.league.controller;
//
//import lol.example.league.dto.GoogleSheetDTO;
//import lol.example.league.dto.GoogleSheetResponseDTO;
//import lol.example.league.service.TestService;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.util.Map;
//
//@RestController
//public class TestController {
//
//    private TestService testService;
//
//    @GetMapping("/check")
//    public String check() {
//        return "Test endpoint";
//    }
//
//
//    @GetMapping("/getData")
//    public Map<Object, Object> readDataFromGoogleSheet() throws GeneralSecurityException, IOException {
//        return testService.getDataFromGoogleSheet();
//    }
//
//    @PostMapping("/createSheet")
//    public String createGoogleSheet(@RequestBody GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        return testService.createSheet(request);
//    }
//
//    @PatchMapping("/updateSheet")
//    public GoogleSheetResponseDTO updateGoogleSheet(@RequestBody GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        return testService.updateSheet(request);
//    }
//
//    @PatchMapping("/updateSheet/email")
//    public GoogleSheetResponseDTO updateSheetAndSendEmail(@RequestBody GoogleSheetDTO request) throws GeneralSecurityException, IOException {
//        return testService.updateSheetAndSendEmail(request);
//    }
//}

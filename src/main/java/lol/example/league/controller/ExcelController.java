package lol.example.league.controller;

import lol.example.league.service.liver.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ExcelController {


    private final HwasunToKosin service;


    private final KosinISGLS service2;


    private final HwasunISGLS service3;


    private final KosinPOD service4;


    private final HwasunPOD service5;

    private final KosinExtraPreOp service6;

    private final HwasunExtraPreOp service7;

    private final SamSungISGLS samSungISGLS;

    private final SamsungPOD samsungPOD;
    private final CGPOD cgpodService;

    @GetMapping("/api/hwasun/excel")
    public void getUserName() {
        service.getWebUserName();
    }

    @GetMapping("/api/kosin/isgls")
    public void getPod() {
        service2.getISGLS();
    }

    @GetMapping("/api/hwasun/isgls")
    public void getHwasunIsgls() {
        service3.getISGLS();
    }

    @GetMapping("/api/kosin/pod")
    public void getKosinPod() {
        service4.getPOD();
    }

    @GetMapping("/api/hwasun/pod")
    public void getHwasunPod() {
        service5.getPOD();
    }

    @GetMapping("/api/kosin/extra")
    public void getKosinExtraPod() {
        service6.getExtraPOD();
    }

    @GetMapping("/api/hwasun/extra")
    public void getHwasunExtraPod() {
        service7.getExtraPOD();
    }

    @GetMapping("/api/samsung/isgls")
    public void getSamsungISGLS() {
        samSungISGLS.getISGLS();
    }

    @GetMapping("/api/samsung/pod")
    public void getSamsungPOD() {
        samsungPOD.getPOD();
    }

    @GetMapping("/api/cg/pod")
    public void getCGPOD() {
        cgpodService.getPOD();
    }
}

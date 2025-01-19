package lol.example.league.service.liver;

import lol.example.league.dto.excel.PatientOPDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SamsungPOD {

    private final String folderPath = "/Users/yangwonjong/python-excel/Samsung/raw/";
    private final String resultPath = "/Users/yangwonjong/python-excel/Samsung/result/";
    private final String patientPath = "/Users/yangwonjong/python-excel/Samsung/patientop/";
    String filePath = "";
    String fileName = null;
    LocalDate date = null;
    DataFormatter formatter = new DataFormatter();
    List<PatientOPDTO.OPdate> opdates = new ArrayList<>();
    PatientOPDTO.OPdate opdate;
    Map<String, LocalDate> patientdate;
    List<PatientOPDTO.PatientPODResult> patientResult = new ArrayList<>();

    private final PodService podService;

    public void getPOD() {

        //1. 전체 환자별 수술 날짜 엑셀 데이터 읽어야 함
        try {
            FileInputStream file = new FileInputStream( patientPath + "patient.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            int rowindex = 0;
            int columnindex = 0;
            //시트 수 (첫번째에만 존재하므로 0을 준다)
            XSSFSheet sheet = workbook.getSheetAt(0);
            //행의 수
            int rows = sheet.getPhysicalNumberOfRows();
            for (rowindex = 0; rowindex < rows; rowindex++) {
                //행을읽는다
                XSSFRow row = sheet.getRow(rowindex);
                // 진단 결과 데이터 읽기 시작
                if (row != null && rowindex >= 1) {
                    // 행별 처리
                    int cells = row.getPhysicalNumberOfCells();
                    for (columnindex = 0; columnindex <= cells; columnindex++) {
                        //셀값을 읽는다
                        XSSFCell cell = row.getCell(columnindex);
                        // 1. 항목별 list에 넣을 dto 생성
                        if (columnindex == 0) {
                            date = cell.getLocalDateTimeCellValue().toLocalDate();
                        } else if (columnindex == 1) {
                            fileName = cell.getStringCellValue();
                        }
//                        opdate = PatientOPDTO.OPdate.builder().patientId(patientId).date(date).build();
                    }
                    opdate = PatientOPDTO.OPdate.builder().fileName(fileName).date(date).build();
                    opdates.add(opdate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 환자별 수술 날짜 정리

        log.info("날짜 집합 결과: " + opdate);
        patientdate = opdates.stream().collect(Collectors.toMap(o -> o.getFileName(), o -> o.getDate()));

//2. 환자별 수치 데이터 를 통해 pre-op 및 POD 데이터 작업 진행
        // 엑셀 파일만 읽기
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File f, String name) {
                return name.contains(".xlsx");
            }
        };
        // 작업할 엑셀이 들어있는 폴더 읽기 진행(루프)
        File rawData = new File(folderPath);
        String[] filenames = rawData.list(filter);
        for (String filename : filenames) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            List<LocalDateTime> dates = new ArrayList<>();
            PatientOPDTO.PatientOP pre = PatientOPDTO.PatientOP.builder().build();
            PatientOPDTO.PatientPODData pod = PatientOPDTO.PatientPODData.create();
            String patientid = "";
            LocalDate opdate = null;

        //2-1. 환자별 데이터 읽기 시작
        if (!filename.contains("~$")) {
            try {
                filePath = folderPath + filename;
                FileInputStream file = new FileInputStream(filePath);
                XSSFWorkbook workbook = new XSSFWorkbook(file);

                patientid = filename.split("\\.")[0];
                log.info("환자 id: " + patientid);
                int rowindex = 0;
                int columnindex = 0;
                opdate = patientdate.get(patientid);
                //시트 수 (첫번째에만 존재하므로 0을 준다)
                XSSFSheet sheet = workbook.getSheetAt(0);
                //행의 수
                int rows = sheet.getPhysicalNumberOfRows();
                for (rowindex = 0; rowindex < rows; rowindex++) {
                    //행을읽는다
                    XSSFRow row = sheet.getRow(rowindex);

                    // 2-1. 환자별 검사 날짜 정보 저장
                    if (row != null && rowindex == 3) {
                        int cells = row.getPhysicalNumberOfCells();
                        for (columnindex = 0; columnindex < cells; columnindex++) {
                            XSSFCell cell = row.getCell(columnindex);
                            if (columnindex > 3) {
//                                    LocalDateTime timetest = LocalDateTime.parse(cell.getStringCellValue(), formatter);
//                                    log.info("this is date: " + timetest);
                                dates.add(LocalDateTime.parse(cell.getStringCellValue(), formatter));
                            }
                        }
                    }

//2-2 데이터 분류 시작. 데이터가 수술일 전 이면 Pre-op 에 적재. 수술 후면 후에 적재 진행 필요
                    //TODO 화순엔 row 변경 필요 + column index 변경 필요
                    if (row != null && rowindex >= 4) {
                        String type = row.getCell(0).getStringCellValue();
                        int cells = row.getPhysicalNumberOfCells();
                        for (columnindex = 0; columnindex < cells; columnindex++) {
                            //셀값을 읽는다
                            XSSFCell cell = row.getCell(columnindex);
                            if (columnindex > 3) {
                                switch (type) {
                                    case "WBC Count, Blood":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setWbc(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getWbc().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "Platelet Count, Blood":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setPlatelet(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getPlatelet().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "Lymphocyte":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setLymphocyte(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getLymphocyte().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "ANC (Absolute Neutrophil Count)":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setNeutrophil(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getNeutrophil().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "PT(INR)":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setPT_INR(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getPT_INR().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "PT( % )":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setPT_P(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getPT_P().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "Protein, Total":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setProtein(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getProtein().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "Albumin":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setAlbumin(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getAlbumin().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "Bilirubin, Total":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setTB(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getTB().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "Bilirubin, Direct":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setDB(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getDB().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "AST":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setAST(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getAST().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "ALT":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setALT(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getALT().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "Creatinine":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setCr(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getCr().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "CRP (C-Reactive Protein)":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setHS_CRP(Double.valueOf(checkDataException(cell.getStringCellValue())));
                                        }
                                        if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pod.getHS_CRP().add(PatientOPDTO.PatientData.builder()
                                                    .date(dates.get(columnindex - 4))
                                                    .data(Double.valueOf(checkDataException(cell.getStringCellValue())))
                                                    .build());
                                        }
                                        break;
                                    case "HBsAg":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setHbsAg(cell.getStringCellValue());
                                        }
                                        break;
                                    case "Anti-HBc Ab, Total":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setHbcAb(cell.getStringCellValue());
                                        }
                                        break;
                                    case "Anti-HBs Ab":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setAntiHbs(cell.getStringCellValue());
                                        }
                                        break;
                                    case "Anti-HCV Ab":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setAntiHCV(cell.getStringCellValue());
                                        }
                                        break;
                                    case "AFP":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setAFP(cell.getStringCellValue());
                                        }
                                        break;
                                    case "PIVKA -II Test":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setPIVKA(cell.getStringCellValue());
                                        }
                                        break;
                                    case "CEA, Serum (Carcinoembryonic Antigen)":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setCEA(cell.getStringCellValue());
                                        }
                                        break;
                                    case "CA 19-9 (Carbohydrate Antigen 19-9)":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setCA19(cell.getStringCellValue());
                                        }
                                        break;
                                    case "ELF(혈청 간섬유화 검사)":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setELF(cell.getStringCellValue());
                                        }
                                        break;
                                    case "ICG(R15)":
                                        if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                            pre.setIGSR15(cell.getStringCellValue());
                                        }
                                        break;
                                }
                            }


                        }
                    }
//                        log.info("데이터 처리 테스트 확인점");
                }
                log.info("데이터 처리 완료");

            } catch (Exception e) {
                e.printStackTrace();
            }


//3. 데이터 분류 후 환자의 pre-op 및 POD 계산 후 총 리스트에 적재 진행
//3-1. 기본 환자 정보와 pre-op 데이터 적재
            PatientOPDTO.PatientOP preOp = PatientOPDTO.PatientOP.builder()
                    .wbc(pre.getWbc() != null ? pre.getWbc() : 77777.0)
                    .Neutrophil(pre.getNeutrophil() != null ? pre.getNeutrophil() : 77777.0)
                    .lymphocyte(pre.getLymphocyte() != null ? pre.getLymphocyte() : 77777.0)
                    .Platelet(pre.getPlatelet() != null ? pre.getPlatelet() : 77777.0)
                    .PT_INR(pre.getPT_INR() != null ? pre.getPT_INR() : 77777.0)
                    .PT_P(pre.getPT_P() != null ? pre.getPT_P() : 77777.0)
                    .protein(pre.getProtein() != null ? pre.getProtein() : 77777.0)
                    .Albumin(pre.getAlbumin() != null ? pre.getAlbumin() : 77777.0)
                    .TB(pre.getTB() != null ? pre.getTB() : 77777.0)
                    .DB(pre.getDB() != null ? pre.getDB() : 77777.0)
                    .AST(pre.getAST() != null ? pre.getAST() : 77777.0)
                    .ALT(pre.getALT() != null ? pre.getALT() : 77777.0)
                    .Cr(pre.getCr() != null ? pre.getCr() : 77777.0)
                    .HS_CRP(pre.getHS_CRP() != null ? pre.getHS_CRP() : 77777.0)
                    .HbsAg(pre.getHbsAg() != null ? pre.getHbsAg() : "NA")
                    .HbcAb(pre.getHbcAb() != null ? pre.getHbcAb() : "NA")
                    .AntiHbs(pre.getAntiHbs() != null ? pre.getAntiHbs() : "NA")
                    .AntiHCV(pre.getAntiHCV() != null ? pre.getAntiHCV() : "NA")
                    .AFP(pre.getAFP() != null ? pre.getAFP() : "NA")
                    .PIVKA(pre.getPIVKA() != null ? pre.getPIVKA() : "NA")
                    .CEA(pre.getCEA() != null ? pre.getCEA() : "NA")
                    .CA19(pre.getCA19() != null ? pre.getCA19() : "NA")
                    .ELF(pre.getELF() != null ? pre.getELF() : "NA")
                    .IGSR15(pre.getIGSR15() != null ? pre.getIGSR15() : "NA")
                    .build();

            PatientOPDTO.PatientPODResult data = PatientOPDTO.PatientPODResult.builder()
                    .patientId(patientid)
                    .opDate(opdate)
                    .preop(preOp)
                    .build();

//3-2. 위 항목별 리스트 처리하면서 POD 데이터 처리 후 적재
            List<PatientOPDTO.PatientData> wbsData = pod.getWbc();
            data.setWbc(podService.getPODS(wbsData, opdate));
            List<PatientOPDTO.PatientData> neuData = pod.getNeutrophil();
            data.setNeutrophil(podService.getPODS(neuData, opdate));
            List<PatientOPDTO.PatientData> lymData = pod.getLymphocyte();
            data.setLymphocyte(podService.getPODS(lymData, opdate));
            List<PatientOPDTO.PatientData> palData = pod.getPlatelet();
            data.setPlatelet(podService.getPODS(palData, opdate));
            List<PatientOPDTO.PatientData> ptiData = pod.getPT_INR();
            data.setPT_INR(podService.getPODS(ptiData, opdate));
            List<PatientOPDTO.PatientData> ptpData = pod.getPT_P();
            data.setPT_P(podService.getPODS(ptpData, opdate));
            List<PatientOPDTO.PatientData> proData = pod.getProtein();
            data.setProtein(podService.getPODS(proData, opdate));
            List<PatientOPDTO.PatientData> albData = pod.getAlbumin();
            data.setAlbumin(podService.getPODS(albData, opdate));
            List<PatientOPDTO.PatientData> tbData = pod.getTB();
            data.setTB(podService.getPODS(tbData, opdate));
            List<PatientOPDTO.PatientData> dbData = pod.getDB();
            data.setDB(podService.getPODS(dbData, opdate));
            List<PatientOPDTO.PatientData> astData = pod.getAST();
            data.setAST(podService.getPODS(astData, opdate));
            List<PatientOPDTO.PatientData> altData = pod.getALT();
            data.setALT(podService.getPODS(altData, opdate));
            List<PatientOPDTO.PatientData> crData = pod.getCr();
            data.setCr(podService.getPODS(crData, opdate));
            List<PatientOPDTO.PatientData> hscData = pod.getHS_CRP();
            data.setHS_CRP(podService.getPODS(hscData, opdate));

            patientResult.add(data);
        }

    }

//4. 총 환자 결과 리스트를 가지고 최종 엑셀 형식으로 데이터 정렬해서 엑셀 파일 생성 진행
        try {
        File file = new File(resultPath + "pod_result.xlsx");
        FileOutputStream fileout = new FileOutputStream(file);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("result");
        // 칼럼 설정
        int preIndex = 110;
        String[] columns = new String[preIndex];
        columns[0]  = "환자ID";
        columns[1]  = "수술일자";
        columns[2]  = "pre_wbc";
        columns[3]  = "pre_Neutrophil";
        columns[4]  = "pre_lymphocyte";
        columns[5]  = "pre_Platelet";
        columns[6]  = "pre_PT_INR";
        columns[7]  = "pre_PT_P";
        columns[8]  = "pre_protein";
        columns[9]  = "pre_Albumin";
        columns[10] = "pre_TB";
        columns[11] = "pre_DB";
        columns[12] = "pre_AST";
        columns[13] = "pre_ALT";
        columns[14] = "pre_Cr";
        columns[15] = "pre_HS_CRP";
        columns[16] = "POD1_wbc";
        columns[17] = "POD1_Neutrophil";
        columns[18] = "POD1_lymphocyte";
        columns[19] = "POD1_Platelet";
        columns[20] = "POD1_PT_INR";
        columns[21] = "POD1_PT_P";
        columns[22] = "POD1_protein";
        columns[23] = "POD1_Albumin";
        columns[24] = "POD1_TB";
        columns[25] = "POD1_DB";
        columns[26] = "POD1_AST";
        columns[27] = "POD1_ALT";
        columns[28] = "POD1_Cr";
        columns[29] = "POD1_HS_CRP";
        columns[30] = "POD3_wbc";
        columns[31] = "POD3_Neutrophil";
        columns[32] = "POD3_lymphocyte";
        columns[33] = "POD3_Platelet";
        columns[34] = "POD3_PT_INR";
        columns[35] = "POD3_PT_P";
        columns[36] = "POD3_protein";
        columns[37] = "POD3_Albumin";
        columns[38] = "POD3_TB";
        columns[39] = "POD3_DB";
        columns[40] = "POD3_AST";
        columns[41] = "POD3_ALT";
        columns[42] = "POD3_Cr";
        columns[43] = "POD3_HS_CRP";
        columns[44] = "POD5_wbc";
        columns[45] = "POD5_Neutrophil";
        columns[46] = "POD5_lymphocyte";
        columns[47] = "POD5_Platelet";
        columns[48] = "POD5_PT_INR";
        columns[49] = "POD5_PT_P";
        columns[50] = "POD5_protein";
        columns[51] = "POD5_Albumin";
        columns[52] = "POD5_TB";
        columns[53] = "POD5_DB";
        columns[54] = "POD5_AST";
        columns[55] = "POD5_ALT";
        columns[56] = "POD5_Cr";
        columns[57] = "POD5_HS_CRP";
        columns[58] = "POD7_wbc";
        columns[59] = "POD7_Neutrophil";
        columns[60] = "POD7_lymphocyte";
        columns[61] = "POD7_Platelet";
        columns[62] = "POD7_PT_INR";
        columns[63] = "POD7_PT_P";
        columns[64] = "POD7_protein";
        columns[65] = "POD7_Albumin";
        columns[66] = "POD7_TB";
        columns[67] = "POD7_DB";
        columns[68] = "POD7_AST";
        columns[69] = "POD7_ALT";
        columns[70] = "POD7_Cr";
        columns[71] = "POD7_HS_CRP";
        columns[72] = "POD10_wbc";
        columns[73] = "POD10_Neutrophil";
        columns[74] = "POD10_lymphocyte";
        columns[75] = "POD10_Platelet";
        columns[76] = "POD10_PT_INR";
        columns[77] = "POD10_PT_P";
        columns[78] = "POD10_protein";
        columns[79] = "POD10_Albumin";
        columns[80] = "POD10_TB";
        columns[81] = "POD10_DB";
        columns[82] = "POD10_AST";
        columns[83] = "POD10_ALT";
        columns[84] = "POD10_Cr";
        columns[85] = "POD10_HS_CRP";
        columns[86] = "POD14_wbc";
        columns[87] = "POD14_Neutrophil";
        columns[88] = "POD14_lymphocyte";
        columns[89] = "POD14_Platelet";
        columns[90] = "POD14_PT_INR";
        columns[91] = "POD14_PT_P";
        columns[92] = "POD14_protein";
        columns[93] = "POD14_Albumin";
        columns[94] = "POD14_TB";
        columns[95] = "POD14_DB";
        columns[96] = "POD14_AST";
        columns[97] = "POD14_ALT";
        columns[98] = "POD14_Cr";
        columns[99] = "POD14_HS_CRP";
        columns[100] = "HbsAg";
        columns[101] = "HbcAb";
        columns[102] = "AntiHbs";
        columns[103] = "AntiHCV";
        columns[104] = "AFP";
        columns[105] = "PIVKA";
        columns[106] = "CEA";
        columns[107] = "CA19";
        columns[108] = "ELF";
        columns[109] = "IGSR15";


        Row header = sheet.createRow(0);
        for (int col = 0; col < columns.length; col++) {
            Cell cell = header.createCell(col);
            cell.setCellValue(columns[col]);
        }

        int rowIndex = 1;
        int colIndex = 0;
        for (PatientOPDTO.PatientPODResult set : patientResult) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(set.getPatientId());
            row.createCell(1).setCellValue(set.getOpDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            row.createCell(2).setCellValue(set.getPreop().getWbc()!=null?set.getPreop().getWbc():null);
            row.createCell(3).setCellValue(set.getPreop().getNeutrophil()!=null?set.getPreop().getNeutrophil():null);
            row.createCell(4).setCellValue(set.getPreop().getLymphocyte()!=null?set.getPreop().getLymphocyte():null);
            row.createCell(5).setCellValue(set.getPreop().getPlatelet()!=null?set.getPreop().getPlatelet():null);
            row.createCell(6).setCellValue(set.getPreop().getPT_INR()!=null?set.getPreop().getPT_INR():null);
            row.createCell(7).setCellValue(set.getPreop().getPT_P()!=null?set.getPreop().getPT_P():null);
            row.createCell(8).setCellValue(set.getPreop().getProtein()!=null?set.getPreop().getProtein():null);
            row.createCell(9).setCellValue(set.getPreop().getAlbumin()!=null?set.getPreop().getAlbumin():null);
            row.createCell(10).setCellValue(set.getPreop().getTB()!=null?set.getPreop().getTB():null);
            row.createCell(11).setCellValue(set.getPreop().getDB()!=null?set.getPreop().getDB():null);
            row.createCell(12).setCellValue(set.getPreop().getAST()!=null?set.getPreop().getAST():null);
            row.createCell(13).setCellValue(set.getPreop().getALT()!=null?set.getPreop().getALT():null);
            row.createCell(14).setCellValue(set.getPreop().getCr()!=null?set.getPreop().getCr():null);
            row.createCell(15).setCellValue(set.getPreop().getHS_CRP()!=null?set.getPreop().getHS_CRP():null);

            row.createCell(16).setCellValue(set.getWbc().getPOD1()!=null?set.getWbc().getPOD1():null);
            row.createCell(17).setCellValue(set.getNeutrophil().getPOD1()!=null?set.getNeutrophil().getPOD1():null);
            row.createCell(18).setCellValue(set.getLymphocyte().getPOD1()!=null?set.getLymphocyte().getPOD1():null);
            row.createCell(19).setCellValue(set.getPlatelet().getPOD1()!=null?set.getPlatelet().getPOD1():null);
            row.createCell(20).setCellValue(set.getPT_INR().getPOD1()!=null?set.getPT_INR().getPOD1():null);
            row.createCell(21).setCellValue(set.getPT_P().getPOD1()!=null?set.getPT_P().getPOD1():null);
            row.createCell(22).setCellValue(set.getProtein().getPOD1()!=null?set.getProtein().getPOD1():null);
            row.createCell(23).setCellValue(set.getAlbumin().getPOD1()!=null?set.getAlbumin().getPOD1():null);
            row.createCell(24).setCellValue(set.getTB().getPOD1()!=null?set.getTB().getPOD1():null);
            row.createCell(25).setCellValue(set.getDB().getPOD1()!=null?set.getDB().getPOD1():null);
            row.createCell(26).setCellValue(set.getAST().getPOD1()!=null?set.getAST().getPOD1():null);
            row.createCell(27).setCellValue(set.getALT().getPOD1()!=null?set.getALT().getPOD1():null);
            row.createCell(28).setCellValue(set.getCr().getPOD1()!=null?set.getCr().getPOD1():null);
            row.createCell(29).setCellValue(set.getHS_CRP().getPOD1()!=null?set.getHS_CRP().getPOD1():null);

            row.createCell(30).setCellValue(set.getWbc().getPOD3()!=null?set.getWbc().getPOD3():null);
            row.createCell(31).setCellValue(set.getNeutrophil().getPOD3()!=null?set.getNeutrophil().getPOD3():null);
            row.createCell(32).setCellValue(set.getLymphocyte().getPOD3()!=null?set.getLymphocyte().getPOD3():null);
            row.createCell(33).setCellValue(set.getPlatelet().getPOD3()!=null?set.getPlatelet().getPOD3():null);
            row.createCell(34).setCellValue(set.getPT_INR().getPOD3()!=null?set.getPT_INR().getPOD3():null);
            row.createCell(35).setCellValue(set.getPT_P().getPOD3()!=null?set.getPT_P().getPOD3():null);
            row.createCell(36).setCellValue(set.getProtein().getPOD3()!=null?set.getProtein().getPOD3():null);
            row.createCell(37).setCellValue(set.getAlbumin().getPOD3()!=null?set.getAlbumin().getPOD3():null);
            row.createCell(38).setCellValue(set.getTB().getPOD3()!=null?set.getTB().getPOD3():null);
            row.createCell(39).setCellValue(set.getDB().getPOD3()!=null?set.getDB().getPOD3():null);
            row.createCell(40).setCellValue(set.getAST().getPOD3()!=null?set.getAST().getPOD3():null);
            row.createCell(41).setCellValue(set.getALT().getPOD3()!=null?set.getALT().getPOD3():null);
            row.createCell(42).setCellValue(set.getCr().getPOD3()!=null?set.getCr().getPOD3():null);
            row.createCell(43).setCellValue(set.getHS_CRP().getPOD3()!=null?set.getHS_CRP().getPOD3():null);

            row.createCell(44).setCellValue(set.getWbc().getPOD5()!=null?set.getWbc().getPOD5():null);
            row.createCell(45).setCellValue(set.getNeutrophil().getPOD5()!=null?set.getNeutrophil().getPOD5():null);
            row.createCell(46).setCellValue(set.getLymphocyte().getPOD5()!=null?set.getLymphocyte().getPOD5():null);
            row.createCell(47).setCellValue(set.getPlatelet().getPOD5()!=null?set.getPlatelet().getPOD5():null);
            row.createCell(48).setCellValue(set.getPT_INR().getPOD5()!=null?set.getPT_INR().getPOD5():null);
            row.createCell(49).setCellValue(set.getPT_P().getPOD5()!=null?set.getPT_P().getPOD5():null);
            row.createCell(50).setCellValue(set.getProtein().getPOD5()!=null?set.getProtein().getPOD5():null);
            row.createCell(51).setCellValue(set.getAlbumin().getPOD5()!=null?set.getAlbumin().getPOD5():null);
            row.createCell(52).setCellValue(set.getTB().getPOD5()!=null?set.getTB().getPOD5():null);
            row.createCell(53).setCellValue(set.getDB().getPOD5()!=null?set.getDB().getPOD5():null);
            row.createCell(54).setCellValue(set.getAST().getPOD5()!=null?set.getAST().getPOD5():null);
            row.createCell(55).setCellValue(set.getALT().getPOD5()!=null?set.getALT().getPOD5():null);
            row.createCell(56).setCellValue(set.getCr().getPOD5()!=null?set.getCr().getPOD5():null);
            row.createCell(57).setCellValue(set.getHS_CRP().getPOD5()!=null?set.getHS_CRP().getPOD5():null);

            row.createCell(58).setCellValue(set.getWbc().getPOD7()!=null?set.getWbc().getPOD7():null);
            row.createCell(59).setCellValue(set.getNeutrophil().getPOD7()!=null?set.getNeutrophil().getPOD7():null);
            row.createCell(60).setCellValue(set.getLymphocyte().getPOD7()!=null?set.getLymphocyte().getPOD7():null);
            row.createCell(61).setCellValue(set.getPlatelet().getPOD7()!=null?set.getPlatelet().getPOD7():null);
            row.createCell(62).setCellValue(set.getPT_INR().getPOD7()!=null?set.getPT_INR().getPOD7():null);
            row.createCell(63).setCellValue(set.getPT_P().getPOD7()!=null?set.getPT_P().getPOD7():null);
            row.createCell(64).setCellValue(set.getProtein().getPOD7()!=null?set.getProtein().getPOD7():null);
            row.createCell(65).setCellValue(set.getAlbumin().getPOD7()!=null?set.getAlbumin().getPOD7():null);
            row.createCell(66).setCellValue(set.getTB().getPOD7()!=null?set.getTB().getPOD7():null);
            row.createCell(67).setCellValue(set.getDB().getPOD7()!=null?set.getDB().getPOD7():null);
            row.createCell(68).setCellValue(set.getAST().getPOD7()!=null?set.getAST().getPOD7():null);
            row.createCell(69).setCellValue(set.getALT().getPOD7()!=null?set.getALT().getPOD7():null);
            row.createCell(70).setCellValue(set.getCr().getPOD7()!=null?set.getCr().getPOD7():null);
            row.createCell(71).setCellValue(set.getHS_CRP().getPOD7()!=null?set.getHS_CRP().getPOD7():null);

            row.createCell(72).setCellValue(set.getWbc().getPOD10()!=null?set.getWbc().getPOD10():null);
            row.createCell(73).setCellValue(set.getNeutrophil().getPOD10()!=null?set.getNeutrophil().getPOD10():null);
            row.createCell(74).setCellValue(set.getLymphocyte().getPOD10()!=null?set.getLymphocyte().getPOD10():null);
            row.createCell(75).setCellValue(set.getPlatelet().getPOD10()!=null?set.getPlatelet().getPOD10():null);
            row.createCell(76).setCellValue(set.getPT_INR().getPOD10()!=null?set.getPT_INR().getPOD10():null);
            row.createCell(77).setCellValue(set.getPT_P().getPOD10()!=null?set.getPT_P().getPOD10():null);
            row.createCell(78).setCellValue(set.getProtein().getPOD10()!=null?set.getProtein().getPOD10():null);
            row.createCell(79).setCellValue(set.getAlbumin().getPOD10()!=null?set.getAlbumin().getPOD10():null);
            row.createCell(80).setCellValue(set.getTB().getPOD10()!=null?set.getTB().getPOD10():null);
            row.createCell(81).setCellValue(set.getDB().getPOD10()!=null?set.getDB().getPOD10():null);
            row.createCell(82).setCellValue(set.getAST().getPOD10()!=null?set.getAST().getPOD10():null);
            row.createCell(83).setCellValue(set.getALT().getPOD10()!=null?set.getALT().getPOD10():null);
            row.createCell(84).setCellValue(set.getCr().getPOD10()!=null?set.getCr().getPOD10():null);
            row.createCell(85).setCellValue(set.getHS_CRP().getPOD10()!=null?set.getHS_CRP().getPOD10():null);

            row.createCell(86).setCellValue(set.getWbc().getPOD14()!=null?set.getWbc().getPOD14():null);
            row.createCell(87).setCellValue(set.getNeutrophil().getPOD14()!=null?set.getNeutrophil().getPOD14():null);
            row.createCell(88).setCellValue(set.getLymphocyte().getPOD14()!=null?set.getLymphocyte().getPOD14():null);
            row.createCell(89).setCellValue(set.getPlatelet().getPOD14()!=null?set.getPlatelet().getPOD14():null);
            row.createCell(90).setCellValue(set.getPT_INR().getPOD14()!=null?set.getPT_INR().getPOD14():null);
            row.createCell(91).setCellValue(set.getPT_P().getPOD14()!=null?set.getPT_P().getPOD14():null);
            row.createCell(92).setCellValue(set.getProtein().getPOD14()!=null?set.getProtein().getPOD14():null);
            row.createCell(93).setCellValue(set.getAlbumin().getPOD14()!=null?set.getAlbumin().getPOD14():null);
            row.createCell(94).setCellValue(set.getTB().getPOD14()!=null?set.getTB().getPOD14():null);
            row.createCell(95).setCellValue(set.getDB().getPOD14()!=null?set.getDB().getPOD14():null);
            row.createCell(96).setCellValue(set.getAST().getPOD14()!=null?set.getAST().getPOD14():null);
            row.createCell(97).setCellValue(set.getALT().getPOD14()!=null?set.getALT().getPOD14():null);
            row.createCell(98).setCellValue(set.getCr().getPOD14()!=null?set.getCr().getPOD14():null);
            row.createCell(99).setCellValue(set.getHS_CRP().getPOD14()!=null?set.getHS_CRP().getPOD14():null);

            row.createCell(100).setCellValue(set.getPreop().getHbsAg());
            row.createCell(101).setCellValue(set.getPreop().getHbcAb());
            row.createCell(102).setCellValue(set.getPreop().getAntiHbs());
            row.createCell(103).setCellValue(set.getPreop().getAntiHCV());
            row.createCell(104).setCellValue(set.getPreop().getAFP());
            row.createCell(105).setCellValue(set.getPreop().getPIVKA());
            row.createCell(106).setCellValue(set.getPreop().getCEA());
            row.createCell(107).setCellValue(set.getPreop().getCA19());
            row.createCell(108).setCellValue(set.getPreop().getELF());
            row.createCell(109).setCellValue(set.getPreop().getIGSR15());

            rowIndex++;
        }
        workbook.write(fileout);
        fileout.close();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public String checkDataException(String data){
        if(data.startsWith("▲") || data.startsWith("▼")){
            if(data.substring(1).startsWith("<") || data.substring(1).startsWith(">")){
                return data.substring(2);
            }
            return data.substring(1);
        }
        else if(data.startsWith("<") || data.startsWith(">") ){
//            log.info("data 처리 전" + data);
//            log.info("data 처리 후" + data.split("\\(")[1].split("\\)")[0]);
            if(data.contains("(")){return data.split("\\(")[1].split("\\)")[0];}
            else{return data.substring(1);}
        }
        else if(data.contains("(")){return data.split("\\(")[1].split("\\)")[0];}
        else{
            return data;
        }
    }

}

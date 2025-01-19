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
public class HwasunExtraPreOp {

    private final String folderPath = "/Users/yangwonjong/python-excel/Hwasun/pod-result/";
    private final String resultPath = "/Users/yangwonjong/python-excel/Hwasun/pod/";
    private final String patientPath = "/Users/yangwonjong/python-excel/Hwasun/patientop/";
    String filePath = "";
    Long patientId = null;
    LocalDate date = null;
//    DataFormatter formatter = new DataFormatter();
//    List<PatientOPDTO.OPdate> opdates = new ArrayList<>();
//    PatientOPDTO.OPdate opdate;
    Map<Long, LocalDate> patientdate;
    List<PatientOPDTO.PatientExtraPODResult> patientResult = new ArrayList<>();

    private final PodService podService;

    public void getExtraPOD() {

//1. 전체 환자별 수술 날짜 엑셀 데이터 읽어야 함
        patientdate = podService.getPatientOpDate(patientPath);


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
            PatientOPDTO.PatientExtraPreOp pre = PatientOPDTO.PatientExtraPreOp.builder().build();
            PatientOPDTO.PatientExtraPODData pod = PatientOPDTO.PatientExtraPODData.create();
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
                    opdate = patientdate.get(Long.valueOf(patientid));
                    //시트 수 (첫번째에만 존재하므로 0을 준다)
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    //행의 수
                    int rows = sheet.getPhysicalNumberOfRows();
                    for (rowindex = 0; rowindex < rows; rowindex++) {
                        //행을읽는다
                        XSSFRow row = sheet.getRow(rowindex);

                        // 2-1. 환자별 검사 날짜 정보 저장
                        if (row != null && rowindex == 0) {
                            int cells = row.getPhysicalNumberOfCells();
                            for (columnindex = 0; columnindex < cells; columnindex++) {
                                XSSFCell cell = row.getCell(columnindex);
                                if (columnindex > 3) {
//                                    LocalDateTime timetest = LocalDateTime.parse(cell.getStringCellValue(), formatter);
//                                    log.info("this is date: " + timetest);
                                    dates.add(LocalDateTime.parse(cell.getStringCellValue().replace('T', ' '), formatter));
                                }
                            }
                        }

//2-2 데이터 분류 시작. 데이터가 수술일 전 이면 Pre-op 에 적재. 수술 후면 후에 적재 진행 필요
                        //TODO 화순엔 row 변경 필요 + column index 변경 필요
                        if (row != null && rowindex >= 1) {
                            String type = row.getCell(0).getStringCellValue();
                            int cells = row.getPhysicalNumberOfCells();
                            for (columnindex = 0; columnindex < cells; columnindex++) {
                                //셀값을 읽는다
                                XSSFCell cell = row.getCell(columnindex);
                                if (columnindex > 3) {
                                    switch (type) {
                                        case "Hgb":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setHgb(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getHgb().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "MCV":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setMCV(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getMCV().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Rh typing":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setRh(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getRh().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "ABO cell typing":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setABO(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getABO().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "eGFR(MDRD)":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setMDRD(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getMDRD().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "monocyte":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setMonocyte(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getMonocyte().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Eosinophil":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setEosinophil(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getEosinophil().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "ALP(Alk Phosphatase)":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setALP(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getALP().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "LDH":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setLDH(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getLDH().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "r-GTP":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setGTP(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getGTP().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "APTT":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setAPTT(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getAPTT().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Cholesterol total":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setTotalCho(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getTotalCho().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Triglyceride":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setTrigly(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getTrigly().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "HDL-Cholesterol":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setHDL(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getHDL().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "LDL-Cholesterol":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setLDL(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getLDL().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Amylase Total":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setAmylase(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getAmylase().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Lipase":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setLipase(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getLipase().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "BUN":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setBUN(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getBUN().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Sodium(Na)":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setSodium(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getSodium().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Potassium(K)":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setPotassium(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getPotassium().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Chloride(Cl)":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setChloride(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getChloride().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Ionized Ca":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setICalcium(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getICalcium().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Corrected Ca":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setTCalcium(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getTCalcium().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Phosphorus(P)":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setInorganic(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getInorganic().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Magnesium(Mg)":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setMagnesium(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getMagnesium().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Glucose":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setGlucose(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getGlucose().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Hb A1c (IFCC)":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setHBA1c(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getHBA1c().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
                                            }
                                            break;
                                        case "Lactic Acid (BGA)":
                                            if (dates.get(columnindex - 4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pre.setLactate(cell.getStringCellValue());
                                            }
                                            if (dates.get(columnindex - 4).toLocalDate().isAfter(opdate) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")) {
                                                pod.getLactate().add(PatientOPDTO.PatientData.builder()
                                                        .date(dates.get(columnindex - 4))
                                                        .data2(cell.getStringCellValue())
                                                        .build());
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
                PatientOPDTO.PatientExtraPreOp preOp = PatientOPDTO.PatientExtraPreOp.builder()
                        .patientId(patientid)
                        .opDate(opdate)
                        .Hgb(pre.getHgb() != null ? pre.getHgb() : "NA")
                        .MCV(pre.getMCV() != null ? pre.getMCV() : "NA")
                        .Rh(pre.getRh() != null ? pre.getRh() : "NA")
                        .ABO(pre.getABO() != null ? pre.getABO() : "NA")
                        .MDRD(pre.getMDRD() != null ? pre.getMDRD() : "NA")
                        .Monocyte(pre.getMonocyte() != null ? pre.getMonocyte() : "NA")
                        .Eosinophil(pre.getEosinophil() != null ? pre.getEosinophil() : "NA")
                        .ALP(pre.getALP() != null ? pre.getALP() : "NA")
                        .LDH(pre.getLDH() != null ? pre.getLDH() : "NA")
                        .GTP(pre.getGTP() != null ? pre.getGTP() : "NA")
                        .aPTT(pre.getAPTT() != null ? pre.getAPTT() : "NA")
                        .TotalCho(pre.getTotalCho() != null ? pre.getTotalCho() : "NA")
                        .Trigly(pre.getTrigly() != null ? pre.getTrigly() : "NA")
                        .HDL(pre.getHDL() != null ? pre.getHDL() : "NA")
                        .LDL(pre.getLDL() != null ? pre.getLDL() : "NA")
                        .Amylase(pre.getAmylase() != null ? pre.getAmylase() : "NA")
                        .Lipase(pre.getLipase() != null ? pre.getLipase() : "NA")
                        .BUN(pre.getBUN() != null ? pre.getBUN() : "NA")
                        .sodium(pre.getSodium() != null ? pre.getSodium() : "NA")
                        .Potassium(pre.getPotassium() != null ? pre.getPotassium() : "NA")
                        .Chloride(pre.getChloride() != null ? pre.getChloride() : "NA")
                        .ICalcium(pre.getICalcium() != null ? pre.getICalcium() : "NA")
                        .TCalcium(pre.getTCalcium() != null ? pre.getTCalcium() : "NA")
                        .Inorganic(pre.getInorganic() != null ? pre.getInorganic() : "NA")
                        .Magnesium(pre.getMagnesium() != null ? pre.getMagnesium() : "NA")
                        .Glucose(pre.getGlucose() != null ? pre.getGlucose() : "NA")
                        .HBA1c(pre.getHBA1c() != null ? pre.getHBA1c() : "NA")
                        .Lactate(pre.getLactate() != null ? pre.getLactate() : "NA")
                        .build();

                PatientOPDTO.PatientExtraPODResult data = PatientOPDTO.PatientExtraPODResult.builder()
                        .patientId(patientid)
                        .opDate(opdate)
                        .preop(preOp)
                        .build();

                List<PatientOPDTO.PatientData> hgbData = pod.getHgb();data.setHgb(podService.getExtraPODS(hgbData, opdate));
                List<PatientOPDTO.PatientData> mcvData = pod.getMCV();data.setMCV(podService.getExtraPODS(mcvData, opdate));
                List<PatientOPDTO.PatientData> rhData = pod.getRh();data.setRh(podService.getExtraPODS(rhData, opdate));
                List<PatientOPDTO.PatientData> aboData = pod.getABO();data.setABO(podService.getExtraPODS(aboData, opdate));
                List<PatientOPDTO.PatientData> mdrdData = pod.getMDRD();data.setMDRD(podService.getExtraPODS(mdrdData, opdate));
                List<PatientOPDTO.PatientData> monoData = pod.getMonocyte();data.setMonocyte(podService.getExtraPODS(monoData, opdate));
                List<PatientOPDTO.PatientData> eosinData = pod.getEosinophil();data.setEosinophil(podService.getExtraPODS(eosinData, opdate));
                List<PatientOPDTO.PatientData> alpData = pod.getALP();data.setALP(podService.getExtraPODS(alpData, opdate));
                List<PatientOPDTO.PatientData> ldhData = pod.getLDH();data.setLDH(podService.getExtraPODS(ldhData, opdate));
                List<PatientOPDTO.PatientData> gtpData = pod.getGTP();data.setGTP(podService.getExtraPODS(gtpData, opdate));
                List<PatientOPDTO.PatientData> apttData = pod.getAPTT();data.setAPTT(podService.getExtraPODS(apttData, opdate));
                List<PatientOPDTO.PatientData> totalcData = pod.getTotalCho();data.setTotalCho(podService.getExtraPODS(totalcData, opdate));
                List<PatientOPDTO.PatientData> tryData = pod.getTrigly();data.setTrigly(podService.getExtraPODS(tryData, opdate));
                List<PatientOPDTO.PatientData> hdlData = pod.getHDL();data.setHDL(podService.getExtraPODS(hdlData, opdate));
                List<PatientOPDTO.PatientData> ldlData = pod.getLDL();data.setLDL(podService.getExtraPODS(ldlData, opdate));
                List<PatientOPDTO.PatientData> ldlcData = pod.getLDLc();data.setLDLc(podService.getExtraPODS(ldlcData, opdate));
                List<PatientOPDTO.PatientData> amyData = pod.getAmylase();data.setAmylase(podService.getExtraPODS(amyData, opdate));
                List<PatientOPDTO.PatientData> lipData = pod.getLipase();data.setLipase(podService.getExtraPODS(lipData, opdate));
                List<PatientOPDTO.PatientData> bunData = pod.getBUN();data.setBUN(podService.getExtraPODS(bunData, opdate));
                List<PatientOPDTO.PatientData> sodData = pod.getSodium();data.setSodium(podService.getExtraPODS(sodData, opdate));
                List<PatientOPDTO.PatientData> potData = pod.getPotassium();data.setPotassium(podService.getExtraPODS(potData, opdate));
                List<PatientOPDTO.PatientData> chlData = pod.getChloride();data.setChloride(podService.getExtraPODS(chlData, opdate));
                List<PatientOPDTO.PatientData> icalData = pod.getICalcium();data.setICalcium(podService.getExtraPODS(icalData, opdate));
                List<PatientOPDTO.PatientData> tcalData = pod.getTCalcium();data.setTCalcium(podService.getExtraPODS(tcalData, opdate));
                List<PatientOPDTO.PatientData> inoData = pod.getInorganic();data.setInorganic(podService.getExtraPODS(inoData, opdate));
                List<PatientOPDTO.PatientData> magData = pod.getMagnesium();data.setMagnesium(podService.getExtraPODS(magData, opdate));
                List<PatientOPDTO.PatientData> gluData = pod.getGlucose();data.setGlucose(podService.getExtraPODS(gluData, opdate));
                List<PatientOPDTO.PatientData> hb1Data = pod.getHBA1c();data.setHBA1c(podService.getExtraPODS(hb1Data, opdate));
                List<PatientOPDTO.PatientData> lacData = pod.getLactate();data.setLactate(podService.getExtraPODS(lacData, opdate));

                patientResult.add(data);
            }

        }

//4. 총 환자 결과 리스트를 가지고 최종 엑셀 형식으로 데이터 정렬해서 엑셀 파일 생성 진행
        try {
            File file = new File(resultPath + "extra_pod_result.xlsx");
            FileOutputStream fileout = new FileOutputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("result");
            // 칼럼 설정
            int preIndex = 205;
            String[] columns = new String[preIndex];
            columns[0]  = "환자ID";
            columns[1]  = "수술일자";
            columns[2]  = "pre_Hgb";
            columns[3]  = "pre_MCV";
            columns[4]  = "pre_Rh";
            columns[5]  = "pre_ABO";
            columns[6]  = "pre_MDRD";
            columns[7]  = "pre_Monocyte";
            columns[8]  = "pre_Eosinophil";
            columns[9]  = "pre_ALP";
            columns[10] = "pre_LDH";
            columns[11] = "pre_GTP";
            columns[12] = "pre_aPTT";
            columns[13] = "pre_TotalCho";
            columns[14] = "pre_Trigly";
            columns[15] = "pre_HDL";
            columns[16] = "pre_LDL";
            columns[17] = "pre_LDLc";
            columns[18] = "pre_Amylase";
            columns[19] = "pre_Lipase";
            columns[20] = "pre_BUN";
            columns[21] = "pre_sodium";
            columns[22] = "pre_Potassium";
            columns[23] = "pre_Chloride";
            columns[24] = "pre_ICalcium";
            columns[25] = "pre_TCalcium";
            columns[26] = "pre_Inorganic";
            columns[27] = "pre_Magnesium";
            columns[28] = "pre_Glucose";
            columns[29] = "pre_HBA1c";
            columns[30] = "pre_Lactate";
            columns[31] = "POD1_Hgb";
            columns[32] = "POD1_MCV";
            columns[33] = "POD1_Rh";
            columns[34] = "POD1_ABO";
            columns[35] = "POD1_MDRD";
            columns[36] = "POD1_Monocyte";
            columns[37] = "POD1_Eosinophil";
            columns[38] = "POD1_ALP";
            columns[39] = "POD1_LDH";
            columns[40] = "POD1_GTP";
            columns[41] = "POD1_aPTT";
            columns[42] = "POD1_TotalCho";
            columns[43] = "POD1_Trigly";
            columns[44] = "POD1_HDL";
            columns[45] = "POD1_LDL";
            columns[46] = "POD1_LDLc";
            columns[47] = "POD1_Amylase";
            columns[48] = "POD1_Lipase";
            columns[49] = "POD1_BUN";
            columns[50] = "POD1_sodium";
            columns[51] = "POD1_Potassium";
            columns[52] = "POD1_Chloride";
            columns[53] = "POD1_ICalcium";
            columns[54] = "POD1_TCalcium";
            columns[55] = "POD1_Inorganic";
            columns[56] = "POD1_Magnesium";
            columns[57] = "POD1_Glucose";
            columns[58] = "POD1_HBA1c";
            columns[59] = "POD1_Lactate";
            columns[60] = "POD3_Hgb";
            columns[61] = "POD3_MCV";
            columns[62] = "POD3_Rh";
            columns[63] = "POD3_ABO";
            columns[64] = "POD3_MDRD";
            columns[65] = "POD3_Monocyte";
            columns[66] = "POD3_Eosinophil";
            columns[67] = "POD3_ALP";
            columns[68] = "POD3_LDH";
            columns[69] = "POD3_GTP";
            columns[70] = "POD3_aPTT";
            columns[71] = "POD3_TotalCho";
            columns[72] = "POD3_Trigly";
            columns[73] = "POD3_HDL";
            columns[74] = "POD3_LDL";
            columns[75] = "POD3_LDLc";
            columns[76] = "POD3_Amylase";
            columns[77] = "POD3_Lipase";
            columns[78] = "POD3_BUN";
            columns[79] = "POD3_sodium";
            columns[80] = "POD3_Potassium";
            columns[81] = "POD3_Chloride";
            columns[82] = "POD3_ICalcium";
            columns[83] = "POD3_TCalcium";
            columns[84] = "POD3_Inorganic";
            columns[85] = "POD3_Magnesium";
            columns[86] = "POD3_Glucose";
            columns[87] = "POD3_HBA1c";
            columns[88] = "POD3_Lactate";
            columns[89]  = "POD5_Hgb";
            columns[90]  = "POD5_MCV";
            columns[91]  = "POD5_Rh";
            columns[92]  = "POD5_ABO";
            columns[93]  = "POD5_MDRD";
            columns[94]  = "POD5_Monocyte";
            columns[95]  = "POD5_Eosinophil";
            columns[96]  = "POD5_ALP";
            columns[97]  = "POD5_LDH";
            columns[98]  = "POD5_GTP";
            columns[99]  = "POD5_aPTT";
            columns[100] = "POD5_TotalCho";
            columns[101] = "POD5_Trigly";
            columns[102] = "POD5_HDL";
            columns[103] = "POD5_LDL";
            columns[104] = "POD5_LDLc";
            columns[105] = "POD5_Amylase";
            columns[106] = "POD5_Lipase";
            columns[107] = "POD5_BUN";
            columns[108] = "POD5_sodium";
            columns[109] = "POD5_Potassium";
            columns[110] = "POD5_Chloride";
            columns[111] = "POD5_ICalcium";
            columns[112] = "POD5_TCalcium";
            columns[113] = "POD5_Inorganic";
            columns[114] = "POD5_Magnesium";
            columns[115] = "POD5_Glucose";
            columns[116] = "POD5_HBA1c";
            columns[117] = "POD5_Lactate";
            columns[118] = "POD7_Hgb";
            columns[119] = "POD7_MCV";
            columns[120] = "POD7_Rh";
            columns[121] = "POD7_ABO";
            columns[122] = "POD7_MDRD";
            columns[123] = "POD7_Monocyte";
            columns[124] = "POD7_Eosinophil";
            columns[125] = "POD7_ALP";
            columns[126] = "POD7_LDH";
            columns[127] = "POD7_GTP";
            columns[128] = "POD7_aPTT";
            columns[129] = "POD7_TotalCho";
            columns[130] = "POD7_Trigly";
            columns[131] = "POD7_HDL";
            columns[132] = "POD7_LDL";
            columns[133] = "POD7_LDLc";
            columns[134] = "POD7_Amylase";
            columns[135] = "POD7_Lipase";
            columns[136] = "POD7_BUN";
            columns[137] = "POD7_sodium";
            columns[138] = "POD7_Potassium";
            columns[139] = "POD7_Chloride";
            columns[140] = "POD7_ICalcium";
            columns[141] = "POD7_TCalcium";
            columns[142] = "POD7_Inorganic";
            columns[143] = "POD7_Magnesium";
            columns[144] = "POD7_Glucose";
            columns[145] = "POD7_HBA1c";
            columns[146] = "POD7_Lactate";
            columns[147] = "POD10_Hgb";
            columns[148] = "POD10_MCV";
            columns[149] = "POD10_Rh";
            columns[150] = "POD10_ABO";
            columns[151] = "POD10_MDRD";
            columns[152] = "POD10_Monocyte";
            columns[153] = "POD10_Eosinophil";
            columns[154] = "POD10_ALP";
            columns[155] = "POD10_LDH";
            columns[156] = "POD10_GTP";
            columns[157] = "POD10_aPTT";
            columns[158] = "POD10_TotalCho";
            columns[159] = "POD10_Trigly";
            columns[160] = "POD10_HDL";
            columns[161] = "POD10_LDL";
            columns[162] = "POD10_LDLc";
            columns[163] = "POD10_Amylase";
            columns[164] = "POD10_Lipase";
            columns[165] = "POD10_BUN";
            columns[166] = "POD10_sodium";
            columns[167] = "POD10_Potassium";
            columns[168] = "POD10_Chloride";
            columns[169] = "POD10_ICalcium";
            columns[170] = "POD10_TCalcium";
            columns[171] = "POD10_Inorganic";
            columns[172] = "POD10_Magnesium";
            columns[173] = "POD10_Glucose";
            columns[174] = "POD10_HBA1c";
            columns[175] = "POD10_Lactate";
            columns[176] = "POD14_Hgb";
            columns[177] = "POD14_MCV";
            columns[178] = "POD14_Rh";
            columns[179] = "POD14_ABO";
            columns[180] = "POD14_MDRD";
            columns[181] = "POD14_Monocyte";
            columns[182] = "POD14_Eosinophil";
            columns[183] = "POD14_ALP";
            columns[184] = "POD14_LDH";
            columns[185] = "POD14_GTP";
            columns[186] = "POD14_aPTT";
            columns[187] = "POD14_TotalCho";
            columns[188] = "POD14_Trigly";
            columns[189] = "POD14_HDL";
            columns[190] = "POD14_LDL";
            columns[191] = "POD14_LDLc";
            columns[192] = "POD14_Amylase";
            columns[193] = "POD14_Lipase";
            columns[194] = "POD14_BUN";
            columns[195] = "POD14_sodium";
            columns[196] = "POD14_Potassium";
            columns[197] = "POD14_Chloride";
            columns[198] = "POD14_ICalcium";
            columns[199] = "POD14_TCalcium";
            columns[200] = "POD14_Inorganic";
            columns[201] = "POD14_Magnesium";
            columns[202] = "POD14_Glucose";
            columns[203] = "POD14_HBA1c";
            columns[204] = "POD14_Lactate";



            Row header = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = header.createCell(col);
                cell.setCellValue(columns[col]);
            }

            int rowIndex = 1;
            int colIndex = 0;
            for (PatientOPDTO.PatientExtraPODResult set : patientResult) {
                Row row = sheet.createRow(rowIndex);
                row.createCell(0).setCellValue(set.getPatientId());
                row.createCell(1).setCellValue(set.getOpDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                //pod
                row.createCell(2).setCellValue( set.getPreop().getHgb());
                row.createCell(3).setCellValue( set.getPreop().getMCV());
                row.createCell(4).setCellValue( set.getPreop().getRh());
                row.createCell(5).setCellValue( set.getPreop().getABO());
                row.createCell(6).setCellValue( set.getPreop().getMDRD());
                row.createCell(7).setCellValue( set.getPreop().getMonocyte());
                row.createCell(8).setCellValue( set.getPreop().getEosinophil());
                row.createCell(9).setCellValue( set.getPreop().getALP());
                row.createCell(10).setCellValue(set.getPreop().getLDH());
                row.createCell(11).setCellValue(set.getPreop().getGTP());
                row.createCell(12).setCellValue(set.getPreop().getAPTT());
                row.createCell(13).setCellValue(set.getPreop().getTotalCho());
                row.createCell(14).setCellValue(set.getPreop().getTrigly());
                row.createCell(15).setCellValue(set.getPreop().getHDL());
                row.createCell(16).setCellValue(set.getPreop().getLDL());
                row.createCell(17).setCellValue(set.getPreop().getLDLc());
                row.createCell(18).setCellValue(set.getPreop().getAmylase());
                row.createCell(19).setCellValue(set.getPreop().getLipase());
                row.createCell(20).setCellValue(set.getPreop().getBUN());
                row.createCell(21).setCellValue(set.getPreop().getSodium());
                row.createCell(22).setCellValue(set.getPreop().getPotassium());
                row.createCell(23).setCellValue(set.getPreop().getChloride());
                row.createCell(24).setCellValue(set.getPreop().getICalcium());
                row.createCell(25).setCellValue(set.getPreop().getTCalcium());
                row.createCell(26).setCellValue(set.getPreop().getInorganic());
                row.createCell(27).setCellValue(set.getPreop().getMagnesium());
                row.createCell(28).setCellValue(set.getPreop().getGlucose());
                row.createCell(29).setCellValue(set.getPreop().getHBA1c());
                row.createCell(30).setCellValue(set.getPreop().getLactate());
                //pod1
                row.createCell(31).setCellValue(set.getHgb().getPOD1());
                row.createCell(32).setCellValue(set.getMCV().getPOD1());
                row.createCell(33).setCellValue(set.getRh().getPOD1());
                row.createCell(34).setCellValue(set.getABO().getPOD1());
                row.createCell(35).setCellValue(set.getMDRD().getPOD1());
                row.createCell(36).setCellValue(set.getMonocyte().getPOD1());
                row.createCell(37).setCellValue(set.getEosinophil().getPOD1());
                row.createCell(38).setCellValue(set.getALP().getPOD1());
                row.createCell(39).setCellValue(set.getLDH().getPOD1());
                row.createCell(40).setCellValue(set.getGTP().getPOD1());
                row.createCell(41).setCellValue(set.getAPTT().getPOD1());
                row.createCell(42).setCellValue(set.getTotalCho().getPOD1());
                row.createCell(43).setCellValue(set.getTrigly().getPOD1());
                row.createCell(44).setCellValue(set.getHDL().getPOD1());
                row.createCell(45).setCellValue(set.getLDL().getPOD1());
                row.createCell(46).setCellValue(set.getLDLc().getPOD1());
                row.createCell(47).setCellValue(set.getAmylase().getPOD1());
                row.createCell(48).setCellValue(set.getLipase().getPOD1());
                row.createCell(49).setCellValue(set.getBUN().getPOD1());
                row.createCell(50).setCellValue(set.getSodium().getPOD1());
                row.createCell(51).setCellValue(set.getPotassium().getPOD1());
                row.createCell(52).setCellValue(set.getChloride().getPOD1());
                row.createCell(53).setCellValue(set.getICalcium().getPOD1());
                row.createCell(54).setCellValue(set.getTCalcium().getPOD1());
                row.createCell(55).setCellValue(set.getInorganic().getPOD1());
                row.createCell(56).setCellValue(set.getMagnesium().getPOD1());
                row.createCell(57).setCellValue(set.getGlucose().getPOD1());
                row.createCell(58).setCellValue(set.getHBA1c().getPOD1());
                row.createCell(59).setCellValue(set.getLactate().getPOD1());
                //pod3
                row.createCell(60).setCellValue(set.getHgb().getPOD3());
                row.createCell(61).setCellValue(set.getMCV().getPOD3());
                row.createCell(62).setCellValue(set.getRh().getPOD3());
                row.createCell(63).setCellValue(set.getABO().getPOD3());
                row.createCell(64).setCellValue(set.getMDRD().getPOD3());
                row.createCell(65).setCellValue(set.getMonocyte().getPOD3());
                row.createCell(66).setCellValue(set.getEosinophil().getPOD3());
                row.createCell(67).setCellValue(set.getALP().getPOD3());
                row.createCell(68).setCellValue(set.getLDH().getPOD3());
                row.createCell(69).setCellValue(set.getGTP().getPOD3());
                row.createCell(70).setCellValue(set.getAPTT().getPOD3());
                row.createCell(71).setCellValue(set.getTotalCho().getPOD3());
                row.createCell(72).setCellValue(set.getTrigly().getPOD3());
                row.createCell(73).setCellValue(set.getHDL().getPOD3());
                row.createCell(74).setCellValue(set.getLDL().getPOD3());
                row.createCell(75).setCellValue(set.getLDLc().getPOD3());
                row.createCell(76).setCellValue(set.getAmylase().getPOD3());
                row.createCell(77).setCellValue(set.getLipase().getPOD3());
                row.createCell(78).setCellValue(set.getBUN().getPOD3());
                row.createCell(79).setCellValue(set.getSodium().getPOD3());
                row.createCell(80).setCellValue(set.getPotassium().getPOD3());
                row.createCell(81).setCellValue(set.getChloride().getPOD3());
                row.createCell(82).setCellValue(set.getICalcium().getPOD3());
                row.createCell(83).setCellValue(set.getTCalcium().getPOD3());
                row.createCell(84).setCellValue(set.getInorganic().getPOD3());
                row.createCell(85).setCellValue(set.getMagnesium().getPOD3());
                row.createCell(86).setCellValue(set.getGlucose().getPOD3());
                row.createCell(87).setCellValue(set.getHBA1c().getPOD3());
                row.createCell(88).setCellValue(set.getLactate().getPOD3());
                //pod5
                row.createCell(89).setCellValue(set.getHgb().getPOD5());
                row.createCell(90).setCellValue(set.getMCV().getPOD5());
                row.createCell(91).setCellValue(set.getRh().getPOD5());
                row.createCell(92).setCellValue(set.getABO().getPOD5());
                row.createCell(93).setCellValue(set.getMDRD().getPOD5());
                row.createCell(94).setCellValue(set.getMonocyte().getPOD5());
                row.createCell(95).setCellValue(set.getEosinophil().getPOD5());
                row.createCell(96).setCellValue(set.getALP().getPOD5());
                row.createCell(97).setCellValue(set.getLDH().getPOD5());
                row.createCell(98).setCellValue(set.getGTP().getPOD5());;
                row.createCell(99).setCellValue(set.getAPTT().getPOD5());
                row.createCell(100).setCellValue(set.getTotalCho().getPOD5());
                row.createCell(101).setCellValue(set.getTrigly().getPOD5());
                row.createCell(102).setCellValue(set.getHDL().getPOD5());
                row.createCell(103).setCellValue(set.getLDL().getPOD5());
                row.createCell(104).setCellValue(set.getLDLc().getPOD5());
                row.createCell(105).setCellValue(set.getAmylase().getPOD5());
                row.createCell(106).setCellValue(set.getLipase().getPOD5());
                row.createCell(107).setCellValue(set.getBUN().getPOD5());
                row.createCell(108).setCellValue(set.getSodium().getPOD5());
                row.createCell(109).setCellValue(set.getPotassium().getPOD5());
                row.createCell(110).setCellValue(set.getChloride().getPOD5());
                row.createCell(111).setCellValue(set.getICalcium().getPOD5());
                row.createCell(112).setCellValue(set.getTCalcium().getPOD5());
                row.createCell(113).setCellValue(set.getInorganic().getPOD5());
                row.createCell(114).setCellValue(set.getMagnesium().getPOD5());
                row.createCell(115).setCellValue(set.getGlucose().getPOD5());
                row.createCell(116).setCellValue(set.getHBA1c().getPOD5());
                row.createCell(117).setCellValue(set.getLactate().getPOD5());
                //pod7
                row.createCell(118).setCellValue(set.getHgb().getPOD7());
                row.createCell(119).setCellValue(set.getMCV().getPOD7());
                row.createCell(120).setCellValue(set.getRh().getPOD7());
                row.createCell(121).setCellValue(set.getABO().getPOD7());
                row.createCell(122).setCellValue(set.getMDRD().getPOD7());
                row.createCell(123).setCellValue(set.getMonocyte().getPOD7());
                row.createCell(124).setCellValue(set.getEosinophil().getPOD7());
                row.createCell(125).setCellValue(set.getALP().getPOD7());
                row.createCell(126).setCellValue(set.getLDH().getPOD7());
                row.createCell(127).setCellValue(set.getGTP().getPOD7());
                row.createCell(128).setCellValue(set.getAPTT().getPOD7());
                row.createCell(129).setCellValue(set.getTotalCho().getPOD7());
                row.createCell(130).setCellValue(set.getTrigly().getPOD7());
                row.createCell(131).setCellValue(set.getHDL().getPOD7());
                row.createCell(132).setCellValue(set.getLDL().getPOD7());
                row.createCell(133).setCellValue(set.getLDLc().getPOD7());
                row.createCell(134).setCellValue(set.getAmylase().getPOD7());
                row.createCell(135).setCellValue(set.getLipase().getPOD7());
                row.createCell(136).setCellValue(set.getBUN().getPOD7());
                row.createCell(137).setCellValue(set.getSodium().getPOD7());
                row.createCell(138).setCellValue(set.getPotassium().getPOD7());
                row.createCell(139).setCellValue(set.getChloride().getPOD7());
                row.createCell(140).setCellValue(set.getICalcium().getPOD7());
                row.createCell(141).setCellValue(set.getTCalcium().getPOD7());
                row.createCell(142).setCellValue(set.getInorganic().getPOD7());
                row.createCell(143).setCellValue(set.getMagnesium().getPOD7());
                row.createCell(144).setCellValue(set.getGlucose().getPOD7());
                row.createCell(145).setCellValue(set.getHBA1c().getPOD7());
                row.createCell(146).setCellValue(set.getLactate().getPOD7());
                //pod10
                row.createCell(147).setCellValue(set.getHgb().getPOD10());
                row.createCell(148).setCellValue(set.getMCV().getPOD10());
                row.createCell(149).setCellValue(set.getRh().getPOD10());
                row.createCell(150).setCellValue(set.getABO().getPOD10());
                row.createCell(151).setCellValue(set.getMDRD().getPOD10());
                row.createCell(152).setCellValue(set.getMonocyte().getPOD10());
                row.createCell(153).setCellValue(set.getEosinophil().getPOD10());
                row.createCell(154).setCellValue(set.getALP().getPOD10());
                row.createCell(155).setCellValue(set.getLDH().getPOD10());
                row.createCell(156).setCellValue(set.getGTP().getPOD10());
                row.createCell(157).setCellValue(set.getAPTT().getPOD10());
                row.createCell(158).setCellValue(set.getTotalCho().getPOD10());
                row.createCell(159).setCellValue(set.getTrigly().getPOD10());
                row.createCell(160).setCellValue(set.getHDL().getPOD10());
                row.createCell(161).setCellValue(set.getLDL().getPOD10());
                row.createCell(162).setCellValue(set.getLDLc().getPOD10());
                row.createCell(163).setCellValue(set.getAmylase().getPOD10());
                row.createCell(164).setCellValue(set.getLipase().getPOD10());
                row.createCell(165).setCellValue(set.getBUN().getPOD10());
                row.createCell(166).setCellValue(set.getSodium().getPOD10());
                row.createCell(167).setCellValue(set.getPotassium().getPOD10());
                row.createCell(168).setCellValue(set.getChloride().getPOD10());
                row.createCell(169).setCellValue(set.getICalcium().getPOD10());
                row.createCell(170).setCellValue(set.getTCalcium().getPOD10());
                row.createCell(171).setCellValue(set.getInorganic().getPOD10());
                row.createCell(172).setCellValue(set.getMagnesium().getPOD10());
                row.createCell(173).setCellValue(set.getGlucose().getPOD10());
                row.createCell(174).setCellValue(set.getHBA1c().getPOD10());
                row.createCell(175).setCellValue(set.getLactate().getPOD10());
                //pod14
                row.createCell(176).setCellValue(set.getHgb().getPOD14());
                row.createCell(177).setCellValue(set.getMCV().getPOD14());
                row.createCell(178).setCellValue(set.getRh().getPOD14());
                row.createCell(179).setCellValue(set.getABO().getPOD14());
                row.createCell(180).setCellValue(set.getMDRD().getPOD14());
                row.createCell(181).setCellValue(set.getMonocyte().getPOD14());
                row.createCell(182).setCellValue(set.getEosinophil().getPOD14());
                row.createCell(183).setCellValue(set.getALP().getPOD14());
                row.createCell(184).setCellValue(set.getLDH().getPOD14());
                row.createCell(185).setCellValue(set.getGTP().getPOD14());
                row.createCell(186).setCellValue(set.getAPTT().getPOD14());
                row.createCell(187).setCellValue(set.getTotalCho().getPOD14());
                row.createCell(188).setCellValue(set.getTrigly().getPOD14());
                row.createCell(189).setCellValue(set.getHDL().getPOD14());
                row.createCell(190).setCellValue(set.getLDL().getPOD14());
                row.createCell(191).setCellValue(set.getLDLc().getPOD14());
                row.createCell(192).setCellValue(set.getAmylase().getPOD14());
                row.createCell(193).setCellValue(set.getLipase().getPOD14());
                row.createCell(194).setCellValue(set.getBUN().getPOD14());
                row.createCell(195).setCellValue(set.getSodium().getPOD14());
                row.createCell(196).setCellValue(set.getPotassium().getPOD14());
                row.createCell(197).setCellValue(set.getChloride().getPOD14());
                row.createCell(198).setCellValue(set.getICalcium().getPOD14());
                row.createCell(199).setCellValue(set.getTCalcium().getPOD14());
                row.createCell(200).setCellValue(set.getInorganic().getPOD14());
                row.createCell(201).setCellValue(set.getMagnesium().getPOD14());
                row.createCell(202).setCellValue(set.getGlucose().getPOD14());
                row.createCell(203).setCellValue(set.getHBA1c().getPOD14());
                row.createCell(204).setCellValue(set.getLactate().getPOD14());
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

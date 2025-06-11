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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KosinISGLS {

    private final String folderPath = "/Users/yangwonjong/python-excel/Kosin/raw3/";
    private final String resultPath = "/Users/yangwonjong/python-excel/Kosin/result/";
    private final String patientPath = "/Users/yangwonjong/python-excel/Kosin/patientop/";
    String filePath = "";
    Long patientId = null;
    LocalDate date = null;
    DataFormatter formatter = new DataFormatter();
    List<PatientOPDTO.OPdate> opdates = new ArrayList<>();
    PatientOPDTO.OPdate opdate;
    Map<Long, LocalDate> patientdate;
    List<PatientOPDTO.ISGLS> patientResult = new ArrayList<>();

    public void getISGLS() {

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
                            patientId = Long.valueOf(cell.getStringCellValue());
                        }
//                        opdate = PatientOPDTO.OPdate.builder().patientId(patientId).date(date).build();
                    }
                    opdate = PatientOPDTO.OPdate.builder().patientId(patientId).date(date).build();
                    opdates.add(opdate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 환자별 수술 날짜 정리

        log.info("날짜 집합 결과: " + opdate);
        patientdate = opdates.stream().collect(Collectors.toMap(o -> o.getPatientId(), o -> o.getDate()));


//2. 환자별 수치 데이터 를 통해 pre-op(PT/Bilirubin) + ISGLS 검사 진행

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
            //회원별로 바뀌는 데이터
            List<LocalDateTime> dates = new ArrayList<>();
            LocalDate opdate = null;
            String patientid = "";
            List<PatientOPDTO.PatientData> preop_pt = new ArrayList<>();
            List<PatientOPDTO.PatientData> preop_bilirubin = new ArrayList<>();
            List<LocalDate> checkDates = new ArrayList<>();
            List<LocalDate> postOP4Dates = new ArrayList<>();
            Map<LocalDate, Double> POST4_PT = new HashMap<>();
            Map<LocalDate, Double> POST4_Bilirubin = new HashMap<>();
            Map<LocalDate, Double> ISGLS_PT = new HashMap<>();
            Map<LocalDate, Double> ISGLS_Bilirubin = new HashMap<>();
            Double PT = null;
            Double Bil = null;

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
// 검사 진행할 수술후 5~21 일 정의한 map 생성
                    for(int i = 5; i<22; i++){
                        ISGLS_PT.put(opdate.plusDays(i), null);
                        ISGLS_Bilirubin.put(opdate.plusDays(i), null);
                        checkDates.add(opdate.plusDays(i));
                    }
// ISGLS 비정상일 경우 첫 해당 수치 비교 검사를 위해 수술 후 4일치 중 TB PT 수치 있는 날 저장을 위한 날짜 정의
                    for(int i = 1; i<5; i++){
                        postOP4Dates.add(opdate.plusDays(i));
                        POST4_PT.put(opdate.plusDays(i), null);
                        POST4_Bilirubin.put(opdate.plusDays(i), null);
                    }
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
                                if(columnindex>3){
                                    LocalDateTime timetest = LocalDateTime.parse(cell.getStringCellValue(), formatter);
//                                    log.info("this is date: " + timetest);
                                    dates.add(LocalDateTime.parse(cell.getStringCellValue(), formatter));
                                }
                            }
                        }

// 2-2. 진단 결과 데이터 읽기 시작
                        if (row != null && rowindex >= 4) {
                            // 행별 처리
                            if (row.getCell(0).getStringCellValue().equals("PT(INR)") || row.getCell(0).getStringCellValue().equals("Bilirubin Total")) {
                                String type = row.getCell(0).getStringCellValue();
                                int cells = row.getPhysicalNumberOfCells();
                                for (columnindex = 0; columnindex < cells; columnindex++) {
                                    //셀값을 읽는다
                                    XSSFCell cell = row.getCell(columnindex);
                                    // 항목별 상한치 기록 필요
                                    if(columnindex==3){
                                        if(type.equals("PT(INR)")){
                                            PT = Double.valueOf(cell.getStringCellValue());
                                        }
                                        else if(type.equals("Bilirubin Total")){
                                            Bil = Double.valueOf(cell.getStringCellValue());
                                        }
                                    }
                                    else if(columnindex>3){
// 2-3. pre-op 데이터 처리용 날짜 + 시간 데이터 집계(수술 전)
                                        if(dates.get(columnindex-4).isBefore(opdate.atStartOfDay()) && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")){
                                            if(type.equals("PT(INR)")){
                                                preop_pt.add(PatientOPDTO.PatientData.builder().date(dates.get(columnindex-4)).data(Double.valueOf(checkDataException(cell.getStringCellValue()))).build());
                                            }
                                            else if(type.equals("Bilirubin Total")){
                                                preop_bilirubin.add(PatientOPDTO.PatientData.builder().date(dates.get(columnindex-4)).data(Double.valueOf(checkDataException(cell.getStringCellValue()))).build());
                                            }
                                        }
// 2-4 ISGLS 비정상 검사를 위한 수술후 수치 적재 (수술후 1~4일)
                                        if(dates.get(columnindex-4).toLocalDate().isAfter(opdate) && dates.get(columnindex-4).toLocalDate().isBefore(opdate.plusDays(5))  && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")){
                                            if(type.equals("PT(INR)")){
                                                if(POST4_PT.get(dates.get(columnindex-4).toLocalDate()) == null){
                                                    POST4_PT.put(dates.get(columnindex-4).toLocalDate(),Double.valueOf(checkDataException(cell.getStringCellValue())));
                                                }
                                                else if(POST4_PT.get(dates.get(columnindex-4).toLocalDate()) < Double.valueOf(checkDataException(cell.getStringCellValue()))){
                                                    POST4_PT.put(dates.get(columnindex-4).toLocalDate(),Double.valueOf(checkDataException(cell.getStringCellValue())));
                                                }
                                            }
                                            else if(type.equals("Bilirubin Total")){
                                                if(POST4_Bilirubin.get(dates.get(columnindex-4).toLocalDate()) == null){
                                                    POST4_Bilirubin.put(dates.get(columnindex-4).toLocalDate(),Double.valueOf(checkDataException(cell.getStringCellValue())));
                                                }
                                                else if(POST4_Bilirubin.get(dates.get(columnindex-4).toLocalDate()) < Double.valueOf(checkDataException(cell.getStringCellValue()))){
                                                    POST4_Bilirubin.put(dates.get(columnindex-4).toLocalDate(),Double.valueOf(checkDataException(cell.getStringCellValue())));
                                                }
                                            }
                                        }
// 2-5. ISGLS 검사용 항복별로 날짜별 max 수치 저장 (수술후 5~21일)
                                        if(dates.get(columnindex-4).toLocalDate().isAfter(opdate.plusDays(4)) && dates.get(columnindex-4).toLocalDate().isBefore(opdate.plusDays(22))  && cell.getStringCellValue() != null && !Objects.equals(cell.getStringCellValue(), "")){
                                            if(type.equals("PT(INR)")){
                                                if(ISGLS_PT.get(dates.get(columnindex-4).toLocalDate()) == null){
                                                    ISGLS_PT.put(dates.get(columnindex-4).toLocalDate(),Double.valueOf(checkDataException(cell.getStringCellValue())));
                                                }
                                                else if(ISGLS_PT.get(dates.get(columnindex-4).toLocalDate()) < Double.valueOf(checkDataException(cell.getStringCellValue()))){
                                                    ISGLS_PT.put(dates.get(columnindex-4).toLocalDate(),Double.valueOf(checkDataException(cell.getStringCellValue())));
                                                }
                                            }
                                            else if(type.equals("Bilirubin Total")){
                                                if(ISGLS_Bilirubin.get(dates.get(columnindex-4).toLocalDate()) == null){
                                                    ISGLS_Bilirubin.put(dates.get(columnindex-4).toLocalDate(),Double.valueOf(checkDataException(cell.getStringCellValue())));
                                                }
                                                else if(ISGLS_Bilirubin.get(dates.get(columnindex-4).toLocalDate()) < Double.valueOf(checkDataException(cell.getStringCellValue()))){
                                                    ISGLS_Bilirubin.put(dates.get(columnindex-4).toLocalDate(),Double.valueOf(checkDataException(cell.getStringCellValue())));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
// 2-6. 집계한 환자별 데이터를 가지고 정상 비정상 계산 시작. 이후 결과물을 환자별로 저장 작업 진행
                log.info("this is to check data");
                //pre-op 데이터 추출
                LocalDateTime prePTDate = preop_pt.get(preop_pt.size()-1).getDate();
                Double prePT = preop_pt.get(preop_pt.size()-1).getData();
                LocalDateTime preBILDate = preop_bilirubin.get(preop_bilirubin.size()-1).getDate();
                Double preBIL = preop_bilirubin.get(preop_bilirubin.size()-1).getData();

                //결과값 변수
                Boolean result  = true;
                LocalDate lastcheckdate = null;
                LocalDate falseDate = null;
                Double falsePT = null;
                Double falseBil = null;
                Double postPT = null;
                Double postBil = null;


                int cnt = 0;
                // pre-op 비정상일 경우
                if(PT < prePT || Bil < preBIL){

                    //수술후 1~4일차 중 5일차에 가장 가까운 PT TB 수치 정의 필요
                    for(int i =0; i < postOP4Dates.size(); i++){
                        if(POST4_PT.get(postOP4Dates.get(i))!= null && POST4_Bilirubin.get(postOP4Dates.get(i))!=null){
                            postPT = POST4_PT.get(postOP4Dates.get(i));
                            postBil = POST4_Bilirubin.get(postOP4Dates.get(i));
                        }
                    }

                    for(int i =0; i < checkDates.size(); i++){
                        //두개 항목 값이 있는 날 찾기
                        if(ISGLS_PT.get(checkDates.get(i))!= null && ISGLS_Bilirubin.get(checkDates.get(i))!=null){
                            // 수술후 1~4일차 데이터 있으면 사용해서 비교. 없으면 pre-op 데이터 이용해서 비교
                            if(cnt==0){
                                if(postPT==null || postBil==null){
                                    if(ISGLS_PT.get(checkDates.get(i)) > prePT && ISGLS_Bilirubin.get(checkDates.get(i)) > preBIL && ISGLS_PT.get(checkDates.get(i)) > PT && ISGLS_Bilirubin.get(checkDates.get(i)) > Bil){
                                        result = false;
                                        falseDate = checkDates.get(i);
                                        falsePT = ISGLS_PT.get(checkDates.get(i));
                                        falseBil = ISGLS_Bilirubin.get(checkDates.get(i));
                                        break;
                                    }
                                }
                                else{
                                    if(ISGLS_PT.get(checkDates.get(i)) > postPT && ISGLS_Bilirubin.get(checkDates.get(i)) > postBil && ISGLS_PT.get(checkDates.get(i)) > PT && ISGLS_Bilirubin.get(checkDates.get(i)) > Bil){
                                        result = false;
                                        falseDate = checkDates.get(i);
                                        falsePT = ISGLS_PT.get(checkDates.get(i));
                                        falseBil = ISGLS_Bilirubin.get(checkDates.get(i));
                                        break;
                                    }
                                }
                                cnt++;
                            }
                            // 이후에는 검사시 마지막으로 2개 데이터 있던 날의 수치와 비교
                            else if(cnt>0){
                                if(ISGLS_PT.get(checkDates.get(i)) > ISGLS_PT.get(lastcheckdate) && ISGLS_Bilirubin.get(checkDates.get(i)) > ISGLS_Bilirubin.get(lastcheckdate) && ISGLS_PT.get(checkDates.get(i)) > PT && ISGLS_Bilirubin.get(checkDates.get(i)) > Bil){
                                    result = false;
                                    falseDate = checkDates.get(i);
                                    falsePT = ISGLS_PT.get(checkDates.get(i));
                                    falseBil = ISGLS_Bilirubin.get(checkDates.get(i));
                                    break;
                                }
                            }
                            lastcheckdate = checkDates.get(i);
                        }
                    }
                }
                // pre-op 정상일 경우
                else{
                    for(int i =0; i < checkDates.size(); i++){
                        if(ISGLS_PT.get(checkDates.get(i))!= null && ISGLS_Bilirubin.get(checkDates.get(i))!=null){
                            if(ISGLS_PT.get(checkDates.get(i)) > PT && ISGLS_Bilirubin.get(checkDates.get(i)) > Bil){
                                result = false;
                                falseDate = checkDates.get(i);
                                falsePT = ISGLS_PT.get(checkDates.get(i));
                                falseBil = ISGLS_Bilirubin.get(checkDates.get(i));
                                break;
                            }
                        }
                    }
                }

                patientResult.add(PatientOPDTO.ISGLS.builder()
                        .patientId(Long.valueOf(patientid))
                        .opDate(opdate)
                        .maxPT(PT)
                        .maxBil(Bil)
                        .prePTDate(prePTDate)
                        .prePT(prePT)
                        .preBILDate(preBILDate)
                        .preBil(preBIL)
                        .checkdate(falseDate)
                        .checkPT(falsePT)
                        .checkBil(falseBil)
                        .result(result)
                        .build());

//                log.info("결과 확인 부분");


                //변수 리셋
                PT = null;
                Bil = null;
                opdate = null;
                preop_pt = new ArrayList<>();
                preop_bilirubin = new ArrayList<>();
                POST4_PT = new HashMap<>();
                POST4_Bilirubin = new HashMap<>();
                ISGLS_PT = new HashMap<>();
                ISGLS_Bilirubin = new HashMap<>();
                dates = new ArrayList<>();
                cnt = 0;
                prePTDate = null;
                prePT = null;
                preBILDate = null;
                preBIL = null;
                lastcheckdate = null;
                falseDate = null;
                falsePT = null;
                falseBil = null;
                postOP4Dates = new ArrayList<>();
                postPT = null;
                postBil = null;
            }
        }

// 3. 집계한 데이터를 가지고 엑셀에 출력 작업 진행
        try {
            File file = new File(resultPath + "isgls_result.xlsx");
            FileOutputStream fileout = new FileOutputStream(file);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("result");
            // 칼럼 설정
            int preIndex = 12;
            String[] columns = new String[preIndex];
            columns[0] = "환자ID";
            columns[1] = "수술일자";
            columns[2] = "PT상한치";
            columns[3] = "Bil상한치";
            columns[4] = "pre-op PT 날짜";
            columns[5] = "pre-op PT";
            columns[6] = "pre-op Bil 날짜";
            columns[7] = "pre-op Bil";
            columns[8] = "검사 날짜";
            columns[9] = "검사 PT";
            columns[10] = "검사 Bil";
            columns[11] = "검사 결과";

            Row header = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = header.createCell(col);
                cell.setCellValue(columns[col]);
            }

            int rowIndex = 1;
            int colIndex = 0;
            for (PatientOPDTO.ISGLS set : patientResult) {
                Row row = sheet.createRow(rowIndex);
                row.createCell(0).setCellValue(set.getPatientId());
                row.createCell(1).setCellValue(set.getOpDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                row.createCell(2).setCellValue(set.getMaxPT());
                row.createCell(3).setCellValue(set.getMaxBil());
                row.createCell(4).setCellValue(set.getPrePTDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                row.createCell(5).setCellValue(set.getPrePT());
                row.createCell(6).setCellValue(set.getPreBILDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                row.createCell(7).setCellValue(set.getPreBil());
                if(set.getCheckdate()!=null){
                    row.createCell(8).setCellValue(set.getCheckdate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
                else{
                    row.createCell(8).setCellValue("");
                }
                if(set.getCheckPT()!=null){
                    row.createCell(9).setCellValue(set.getCheckPT());
                }
                else{
                    row.createCell(9).setCellValue("");
                }
                if(set.getCheckBil()!=null){
                    row.createCell(10).setCellValue(set.getCheckBil());
                }
                else{
                    row.createCell(10).setCellValue("");
                }
                row.createCell(11).setCellValue(set.getResult());

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
            return data.substring(1);
        }
        else if(data.startsWith("<")){
            log.info("data 처리 전" + data);
            log.info("data 처리 후" + data.split("\\(")[1].split("\\)")[0]);
            return data.split("\\(")[1].split("\\)")[0];
        }
        else{
            return data;
        }
    }
}

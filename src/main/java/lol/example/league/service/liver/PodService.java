package lol.example.league.service.liver;


import lol.example.league.dto.excel.PatientOPDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PodService {


    public PatientOPDTO.PODSet getPODS(List<PatientOPDTO.PatientData> data, LocalDate opdate){
//        List<PatientOPDTO.PatientData> pdatas = data;
        CopyOnWriteArrayList<PatientOPDTO.PatientData> pdatas = new CopyOnWriteArrayList<PatientOPDTO.PatientData>(data);
        PatientOPDTO.PODSet result = PatientOPDTO.PODSet.builder().build();
        for(PatientOPDTO.PatientData pdata : pdatas){
//3-3. 일단 해당 일자 데이터가 있는지 1차 검증을 시작. 이후 없는 날짜들을 선별해서 +- 일수 범위에 해당하는 데이터 있는지 조회
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(1)) && result.getPOD1()==null){
                result.setPOD1(pdata.getData());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(3)) && result.getPOD3()==null){
                result.setPOD3(pdata.getData());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(5)) && result.getPOD5()==null){
                result.setPOD5(pdata.getData());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(7)) && result.getPOD7()==null){
                result.setPOD7(pdata.getData());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(10)) && result.getPOD10()==null){
                result.setPOD10(pdata.getData());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(14)) && result.getPOD14()==null) {
                result.setPOD14(pdata.getData());
                pdatas.remove(pdata);
            }
        }

//3-3. 위 작업을 통해 당일 데이터가 없다면 pod 별로 +- 일수 탐색을 해야함. 14 는 1일 그 외는 2일 범위로 찾아야 함.
        if(result.getPOD1()==null){
            LocalDate podDate = opdate.plusDays(1);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD1(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD1(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD3()==null){
            LocalDate podDate = opdate.plusDays(3);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD3(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD3(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD5()==null){
            LocalDate podDate = opdate.plusDays(5);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD5(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD5(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD7()==null){
            LocalDate podDate = opdate.plusDays(7);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD7(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD7(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD10()==null){
            LocalDate podDate = opdate.plusDays(10);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD10(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD10(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        Long compare = null;
        if(result.getPOD14()==null){
            LocalDate podDate = opdate.plusDays(14);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 2){
                        if(compare == null) {
                            result.setPOD14(pdata.getData());
                            pdatas.remove(pdata);
                            compare = ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate);

                        }
                        else if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) < compare){
                            result.setPOD14(pdata.getData());
                            pdatas.remove(pdata);
                            compare = ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate);
                            break;
                        }
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(result.getPOD14() != null){
                        break;
                    }
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 2){
                        result.setPOD14(pdata.getData());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD1()==null) result.setPOD1(77777.0);
        if(result.getPOD3()==null) result.setPOD3(77777.0);
        if(result.getPOD5()==null) result.setPOD5(77777.0);
        if(result.getPOD7()==null) result.setPOD7(77777.0);
        if(result.getPOD10()==null) result.setPOD10(77777.0);
        if(result.getPOD14()==null) result.setPOD14(77777.0);
        return result;
    }


    public Map<Long, LocalDate> getPatientOpDate(String patientPath){

        LocalDate date = null;
        Long patientId = null;
        List<PatientOPDTO.OPdate> opdates = new ArrayList<>();
        PatientOPDTO.OPdate opdate = null;

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
        return opdates.stream().collect(Collectors.toMap(o -> o.getPatientId(), o -> o.getDate()));
    }

    public PatientOPDTO.ExtraPODSet getExtraPODS(List<PatientOPDTO.PatientData> data, LocalDate opdate){
//        List<PatientOPDTO.PatientData> pdatas = data;
        CopyOnWriteArrayList<PatientOPDTO.PatientData> pdatas = new CopyOnWriteArrayList<PatientOPDTO.PatientData>(data);
        PatientOPDTO.ExtraPODSet result = PatientOPDTO.ExtraPODSet.builder().build();
        for(PatientOPDTO.PatientData pdata : pdatas){
//3-3. 일단 해당 일자 데이터가 있는지 1차 검증을 시작. 이후 없는 날짜들을 선별해서 +- 일수 범위에 해당하는 데이터 있는지 조회
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(1)) && result.getPOD1()==null){
                result.setPOD1(pdata.getData2());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(3)) && result.getPOD3()==null){
                result.setPOD3(pdata.getData2());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(5)) && result.getPOD5()==null){
                result.setPOD5(pdata.getData2());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(7)) && result.getPOD7()==null){
                result.setPOD7(pdata.getData2());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(10)) && result.getPOD10()==null){
                result.setPOD10(pdata.getData2());
                pdatas.remove(pdata);
            }
            if(pdata.getDate().toLocalDate().equals(opdate.plusDays(14)) && result.getPOD14()==null) {
                result.setPOD14(pdata.getData2());
                pdatas.remove(pdata);
            }
        }

//3-3. 위 작업을 통해 당일 데이터가 없다면 pod 별로 +- 일수 탐색을 해야함. 14 는 1일 그 외는 2일 범위로 찾아야 함.
        if(result.getPOD1()==null){
            LocalDate podDate = opdate.plusDays(1);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD1(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD1(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD3()==null){
            LocalDate podDate = opdate.plusDays(3);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD3(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD3(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD5()==null){
            LocalDate podDate = opdate.plusDays(5);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD5(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD5(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD7()==null){
            LocalDate podDate = opdate.plusDays(7);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD7(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD7(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD10()==null){
            LocalDate podDate = opdate.plusDays(10);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 1){
                        result.setPOD10(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 1){
                        result.setPOD10(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        Long compare = null;
        if(result.getPOD14()==null){
            LocalDate podDate = opdate.plusDays(14);
            for(PatientOPDTO.PatientData pdata : pdatas){
                if(pdata.getDate().toLocalDate().isBefore(podDate)){
                    if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) <= 2){
                        if(compare == null) {
                            result.setPOD14(pdata.getData2());
                            pdatas.remove(pdata);
                            compare = ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate);

                        }
                        else if(ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate) < compare){
                            result.setPOD14(pdata.getData2());
                            pdatas.remove(pdata);
                            compare = ChronoUnit.DAYS.between(pdata.getDate().toLocalDate(),podDate);
                            break;
                        }
                    }
                }
                else if(pdata.getDate().toLocalDate().isAfter(podDate)){
                    if(result.getPOD14() != null){
                        break;
                    }
                    if(ChronoUnit.DAYS.between(podDate, pdata.getDate().toLocalDate()) <= 2){
                        result.setPOD14(pdata.getData2());
                        pdatas.remove(pdata);
                        break;
                    }
                }
            }
        }
        if(result.getPOD1()==null) result.setPOD1("77777.0");
        if(result.getPOD3()==null) result.setPOD3("77777.0");
        if(result.getPOD5()==null) result.setPOD5("77777.0");
        if(result.getPOD7()==null) result.setPOD7("77777.0");
        if(result.getPOD10()==null) result.setPOD10("77777.0");
        if(result.getPOD14()==null) result.setPOD14("77777.0");
        return result;
    }






}

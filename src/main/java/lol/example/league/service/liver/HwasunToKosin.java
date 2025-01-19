package lol.example.league.service.liver;

import lol.example.league.dto.excel.DiagnosisDTO;
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
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HwasunToKosin {

    private final String folderPath = "/Users/yangwonjong/python-excel/Hwasun/pre-raw/";
    private final String resultPath = "/Users/yangwonjong/python-excel/Hwasun/pre-result/";

    //화순 데이터를 고신 데이터 포맷으로 변경 작업 진행
    public void getWebUserName() {

        String filePath = "";
        String[] INR = new String[3];
        String[] TB = new String[3];
        // TODO 여기 List 명은 고신 버전으로 바꿔놔야 함
        List<String> preset = new ArrayList<>(Arrays.asList("WBC count", "Neutrophil", "Lymphocyte", "Platelet Count", "PT(INR)", "PT(%)", "Total protein", "Albumin",
                "Bilirubin total", "Direct Bilirubin", "AST", "ALT", "Cr(Creatinine)", "HS-CRP",
                "HBs Ag", "Anti-HBs", "Anti-HBc", "Anti-HCV", "AFP (CLIA)", "PIVKA-2", "CEA", "CA 19-9", "ELF", "ICG R15",
                "Hgb", "MCV", "Rh typing", "ABO cell typing", "eGFR(MDRD)", "monocyte", "Eosinophil", "ALP(Alk Phosphatase)",
                "LDH", "r-GTP", "APTT", "Cholesterol total", "Triglyceride", "HDL-Cholesterol", "LDL-Cholesterol", "Calculated LDL-C",
                "Amylase Total", "Lipase", "BUN", "Sodium(Na)", "Potassium(K)", "Chloride(Cl)", "Ionized Ca", "Corrected Ca",
                "Phosphorus(P)", "Magnesium(Mg)", "Glucose", "Hb A1c (IFCC)", "Lactic Acid (BGA)"));

//고신 ver
//        ("WBC Count", "Platelet Count", "Lymphocyte", "monocyte", "ANC",
//                "PT(INR)", "PT(%)", "APTT", "Fibrinogen", "FDP", "D-dimer",
//                "Total protein", "Albumin", "Cholesterol total", "Triglyceride",
//                "HDL-Cholesterol", "LDL-Cholesterol", "Calculated LDL-C",
//                "Bilirubin Total", "Direct Bilirubin", "ALP(Alk Phosphatase)",
//                "r-GTP", "AST", "ALT", "LDH", "Amylase Total", "Lipase", "Glucose",
//                "BUN", "Cr(Creatinine)", "eGFR(MDRD)", "CKD-EPI", "Sodium(Na)",
//                "Potassium(K)", "Chloride(Cl)", "Total CO2", "Cystatin-C",
//                "Cystatin-C, based GFR", "Uric acid", "Calcium(Ca)", "Corrected Ca",
//                "Phosphorus(P)", "Ca*P", "Magnesium(Mg)", "HS-CRP", "HBA1c (NGSP)",
//                "Hb A1c (IFCC)", "eAG", "Lactic Acid (BGA)", "Ionized Ca", "CEA",
//                "CA 19-9", "VDRL(RPR)-정밀", "HBs Ag");

//화순 ver
//        ("WBC count", "Neutrophil", "Lymphocyte", "Platelet Count", "PT(INR)", "PT(%)", "Total protein", "Albumin",
//          "Bilirubin total", "Direct Bilirubin", "AST", "ALT", "Cr(Creatinine)", "HS-CRP",
//          "HBs Ag", "Anti-HBs", "Anti-HBc", "Anti-HCV", "AFP (CLIA)", "PIVKA-2", "CEA", "CA 19-9", "ELF", "ICG R15",
//          "Hgb", "MCV", "Rh typing", "ABO cell typing", "eGFR(MDRD)", "monocyte", "Eosinophil", "ALP(Alk Phosphatase)",
//          "LDH", "r-GTP", "APTT", "Cholesterol total", "Triglyceride", "HDL-Cholesterol", "LDL-Cholesterol", "Calculated LDL-C",
//          "Amylase Total", "Lipase", "BUN", "Sodium(Na)", "Potassium(K)", "Chloride(Cl)", "Ionized Ca", "Corrected Ca",
//          "Phosphorus(P)", "Magnesium(Mg)", "Glucose", "Hb A1c (IFCC)", "Lactic Acid (BGA)")
//
//                  "ANC",
//                  "Fibrinogen", "FDP", "D-dimer",
//                  "Glucose",
//                "Cr(Creatinine)", "CKD-EPI",
//                 "Total CO2", "Cystatin-C",
//                "Cystatin-C, based GFR", "Uric acid", "Calcium(Ca)",
//                 "Ca*P",  "HBA1c (NGSP)",
//                 "eAG",
//                "VDRL(RPR)-정밀");

        String type ="";
        String result ="";
        LocalDateTime time = null;

        DataFormatter formatter = new DataFormatter();

        File list = new File(folderPath);
        // 엑셀 파일만 읽기
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File f, String name) {
                return name.contains(".xlsx");
            }
        };

        // 작업할 엑셀이 들어있는 폴더 읽기 진행(루프)
        String[] filenames = list.list(filter);
        for (String filename : filenames) {
            List<LocalDateTime> dateColumn = new ArrayList<>();
//        ("WBC count", "Neutrophil", "Lymphocyte", "Platelet Count", "PT(INR)", "PT(%)", "Total protein", "Albumin",
//          "Bilirubin total", "Direct Bilirubin", "AST", "ALT", "Cr(Creatinine)", "HS-CRP",
            Map<LocalDateTime, DiagnosisDTO> WBC_countMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> NeutrophilMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> LymphocyteMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Platelet_countMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> PT_INRMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> PTMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> proteinMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> AlbuminMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Bilirubin_totalMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Direct_BilirubinMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> ASTMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> ALTMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> CreatinineMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> HS_CRPMap = new HashMap<>();
//          "HBs Ag", "Anti-HBs", "Anti-HBc", "Anti-HCV", "AFP (CLIA)", "PIVKA-2", "CEA", "CA 19-9", "ELF", "ICG R15",
            Map<LocalDateTime, DiagnosisDTO> HBs_AgMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Anti_HBsMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Anti_HBcMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Anti_HCVMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> AFP_CLIAMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> PIVKA_2Map = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> CEAMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> CA_19Map = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> ELFMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> ICG_R15Map = new HashMap<>();
//          "Hgb", "MCV", "Rh typing", "ABO cell typing", "eGFR(MDRD)", "monocyte", "Eosinophil", "ALP(Alk Phosphatase)",
//          "LDH", "r-GTP", "APTT", "Cholesterol total", "Triglyceride", "HDL-Cholesterol", "LDL-Cholesterol", "Calculated LDL-C",
            Map<LocalDateTime, DiagnosisDTO> HgbMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> MCVMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Rh_typingMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> ABO_cellMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> eGFRMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> monocyteMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> EosinophilMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> ALPMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> LDHMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> r_GTPMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> APTTMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Cholesterol_totalMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> TriglycerideMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> HDLMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> LDLMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> LDL_CMap = new HashMap<>();
//          "Amylase Total", "Lipase", "BUN", "Sodium(Na)", "Potassium(K)", "Chloride(Cl)", "Ionized Ca", "Corrected Ca",
//          "Phosphorus(P)", "Magnesium(Mg)", "Glucose", "Hb A1c (IFCC)", "Lactic Acid (BGA)")
            Map<LocalDateTime, DiagnosisDTO> AmylaseMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> LipaseMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> BUNMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> SodiumMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> PotassiumMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> ChlorideMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Ionized_CaMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> Corrected_CaMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> PhosphorusMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> MagnesiumMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> GlucoseMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> HbA1cMap = new HashMap<>();
            Map<LocalDateTime, DiagnosisDTO> LacticMap = new HashMap<>();

//  STEP 1 : 환자 데이터 읽어 전처리 작업 1차 진행
            System.out.println("filename : " + filename);
            // 파일별 엑셀 읽시 시작
            if (!filename.contains("~$")) {
                try {
                    filePath = folderPath + filename;
                    FileInputStream file = new FileInputStream(filePath);
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
//                            log.info("this is row number" + rowindex);

                            // 진단 결과 데이터 읽기 시작
                            if (row != null && rowindex >= 4) {
                                // 행별 처리
                                if (row.getCell(1).getLocalDateTimeCellValue() != null) {

                                    int cells = row.getPhysicalNumberOfCells();
                                    for (columnindex = 0; columnindex <= cells; columnindex++) {
                                        //셀값을 읽는다
                                        XSSFCell cell = row.getCell(columnindex);
//                                    log.info("this is cell data" + cell);
                                        // 1. 항목별 list에 넣을 dto 생성
                                        if (columnindex == 1) {
                                            time = cell.getLocalDateTimeCellValue();
                                            if (!dateColumn.contains(cell.getLocalDateTimeCellValue())) {
                                                dateColumn.add(cell.getLocalDateTimeCellValue());
                                            }
                                        } else if (columnindex == 2) {
                                            type = cell.getStringCellValue();
                                        } else if (columnindex == 3) {
//                                    result = cell.getStringCellValue();
                                            result = formatter.formatCellValue(cell);
                                        } else if (columnindex == 5) {
                                            if(type.equals("PT")){
                                                String arr[] = cell.getStringCellValue().split("~");
                                                String arr2[] = arr[1].split("/");
                                                String arr3[] = arr2[0].split(" ");
                                                INR[0] = arr3[1];
                                                INR[1] = arr3[0];
                                            }
                                            else if(type.equals("Total bilirubin")){
                                                String arr[] = cell.getStringCellValue().split("~");
                                                String arr2[] = arr[1].split(" ");
                                                TB[0] = arr2[1];
                                                TB[1] = arr2[0];
                                            }
                                        }



//                            System.out.println(rowindex+"번 행 : "+columnindex+"번 열 값은: "+value);
                                    }
                                    // 리스트 객체 생성
                                    DiagnosisDTO temp = DiagnosisDTO.builder()
                                            .result(result)
                                            .type(type)
                                            .time(time)
                                            .build();
                                    // 항목별 임시 리스트 생성
                                    // TODO 아예 map 데이터를 여기서 만들까. 날짜 겹치면/비슷한 이름 데이터는 선착순 1개만 적재
                                    // TODO 비슷한 이름 검증할 리스트들 생성 할 필요 있어 보임 (여기서 중복 화순 데이터를 고신에 맞춰 map 에 적재)
                                    if (temp.getType().equals("WBC count")){if(!WBC_countMap.containsKey(time)){WBC_countMap.put(time,temp);}}
                                    else if (temp.getType().equals("Neutrophil") || temp.getType().equals("Neutrophils")) {if (!NeutrophilMap.containsKey(time)) {NeutrophilMap.put(time, temp);}}
                                    else if (temp.getType().equals("Lymphocyte")|| temp.getType().equals("Lymphocytes")) {if (!LymphocyteMap.containsKey(time)) {LymphocyteMap.put(time, temp);}}
                                    else if (temp.getType().equals("Platelet count")) {if (!Platelet_countMap.containsKey(time)) {Platelet_countMap.put(time, temp);}}
                                    else if (temp.getType().equals("PT INR")) {if (!PT_INRMap.containsKey(time)) {PT_INRMap.put(time, temp);}}
                                    else if (temp.getType().equals("PT")) {if (!PTMap.containsKey(time)) {PTMap.put(time, temp);}}
                                    else if (temp.getType().equals("Total protein")){if(!proteinMap.containsKey(time)){proteinMap.put(time,temp);}}
                                    else if (temp.getType().equals("Albumin")){if(!AlbuminMap.containsKey(time)){AlbuminMap.put(time,temp);}}
                                    else if (temp.getType().equals("Total bilirubin")){if(!Bilirubin_totalMap.containsKey(time)){Bilirubin_totalMap.put(time,temp);}}
                                    else if (temp.getType().equals("Direct bilirubin")){if(!Direct_BilirubinMap.containsKey(time)){Direct_BilirubinMap.put(time,temp);}}
                                    else if (temp.getType().equals("AST")){if(!ASTMap.containsKey(time)){ASTMap.put(time,temp);}}
                                    else if (temp.getType().equals("ALT")){if(!ALTMap.containsKey(time)){ALTMap.put(time,temp);}}
                                    else if (temp.getType().equals("Creatinine")){if(!CreatinineMap.containsKey(time)){CreatinineMap.put(time,temp);}}
                                    else if (temp.getType().equals("CRP (Quantitation)") || temp.getType().equals("CRP2 (high sensitivity)")){if(!HS_CRPMap.containsKey(time)){HS_CRPMap.put(time,temp);}}
                                    else if (temp.getType().equals("HBsAg")){if(!HBs_AgMap.containsKey(time)){HBs_AgMap.put(time,temp);}}
                                    else if (temp.getType().equals("Anti-HBs")){if(!Anti_HBsMap.containsKey(time)){Anti_HBsMap.put(time,temp);}}
                                    else if (temp.getType().equals("Anti-HBc") || temp.getType().equals("(학동) Anti-HBc IgM")){if(!Anti_HBcMap.containsKey(time)){Anti_HBcMap.put(time,temp);}}
                                    else if (temp.getType().equals("Anti-HCV")){if(!Anti_HCVMap.containsKey(time)){Anti_HCVMap.put(time,temp);}}
                                    else if (temp.getType().equals("AFP (CLIA)") || temp.getType().equals("AFP (CLIA) (Serum)")){if(!AFP_CLIAMap.containsKey(time)){AFP_CLIAMap.put(time,temp);}}
                                    else if (temp.getType().equals("PIVKA-2")){if(!PIVKA_2Map.containsKey(time)){PIVKA_2Map.put(time,temp);}}
                                    else if (temp.getType().equals("CEA (CLIA)") || temp.getType().equals("CEA (CLIA)(Serum)")){if(!CEAMap.containsKey(time)){CEAMap.put(time,temp);}}
                                    else if (temp.getType().equals("CA 19-9")){if(!CA_19Map.containsKey(time)){CA_19Map.put(time,temp);}}
                                    else if (temp.getType().equals("ELF(혈청간섬유화검사)")){if(!ELFMap.containsKey(time)){ELFMap.put(time,temp);}}
                                    else if (temp.getType().equals("(학동) ICG R15") || temp.getType().equals("ICG R15")){if(!ICG_R15Map.containsKey(time)){ICG_R15Map.put(time,temp);}}
                                    else if (temp.getType().equals("Hgb") || temp.getType().equals("Hgb (ctHb)")) {if (!HgbMap.containsKey(time)) {HgbMap.put(time, temp);}}
                                    else if (temp.getType().equals("MCV")){if(!MCVMap.containsKey(time)){MCVMap.put(time,temp);}}
                                    else if (temp.getType().equals("Rh typing")){if(!Rh_typingMap.containsKey(time)){Rh_typingMap.put(time,temp);}}
                                    else if (temp.getType().equals("ABO cell typing")){if(!ABO_cellMap.containsKey(time)){ABO_cellMap.put(time,temp);}}
                                    else if (temp.getType().equals("MDRD-eGFR")){if(!eGFRMap.containsKey(time)){eGFRMap.put(time,temp);}}
                                    else if (temp.getType().equals("Monocyte") || temp.getType().equals("Monocytes")) {if (!monocyteMap.containsKey(time)) {monocyteMap.put(time, temp);}}
                                    else if (temp.getType().equals("Eosinophil") || temp.getType().equals("Eosinophils")) {if (!EosinophilMap.containsKey(time)) {EosinophilMap.put(time, temp);}}
                                    else if (temp.getType().equals("Alkaline phosphatase")){if(!ALPMap.containsKey(time)){ALPMap.put(time,temp);}}
                                    else if (temp.getType().equals("LDH (Lactate dehydrogenase)")){if(!LDHMap.containsKey(time)){LDHMap.put(time,temp);}}
                                    else if (temp.getType().equals("gamma-GTP")){if(!r_GTPMap.containsKey(time)){r_GTPMap.put(time,temp);}}
                                    else if (temp.getType().equals("aPTT")){if(!APTTMap.containsKey(time)){APTTMap.put(time,temp);}}
                                    else if (temp.getType().equals("Total cholesterol")){if(!Cholesterol_totalMap.containsKey(time)){Cholesterol_totalMap.put(time,temp);}}
                                    else if (temp.getType().equals("Triglyceride")){if(!TriglycerideMap.containsKey(time)){TriglycerideMap.put(time,temp);}}
                                    else if (temp.getType().equals("HDL-cholesterol")){if(!HDLMap.containsKey(time)){HDLMap.put(time,temp);}}
                                    else if (temp.getType().equals("LDL- Cholesterol")){if(!LDLMap.containsKey(time)){LDLMap.put(time,temp);}}
//                                    else if (temp.getType().equals("aPTT")){if(!APTTMap.containsKey(time)){APTTMap.put(time,temp);}}
                                    else if (temp.getType().equals("Amylase")){if(!AmylaseMap.containsKey(time)){AmylaseMap.put(time,temp);}}
                                    else if (temp.getType().equals("Lipase")){if(!LipaseMap.containsKey(time)){LipaseMap.put(time,temp);}}
                                    else if (temp.getType().equals("BUN")){if(!BUNMap.containsKey(time)){BUNMap.put(time,temp);}}
                                    else if (temp.getType().equals("Sodium") || temp.getType().equals("Na+")) {if (!SodiumMap.containsKey(time)) {SodiumMap.put(time, temp);}}
                                    else if (temp.getType().equals("Potassium") || temp.getType().equals("K+")) {if (!PotassiumMap.containsKey(time)) {PotassiumMap.put(time, temp);}}
                                    else if (temp.getType().equals("Chloride") || temp.getType().equals("Cl-")) {if (!ChlorideMap.containsKey(time)) {ChlorideMap.put(time, temp);}}
                                    else if (temp.getType().equals("Ionized calcium") || temp.getType().equals("Ca++")) {if (!Ionized_CaMap.containsKey(time)) {Ionized_CaMap.put(time, temp);}}
                                    else if (temp.getType().equals("Total calcium") || temp.getType().equals("Ca++ (cCa++)")) {if (!Corrected_CaMap.containsKey(time)) {Corrected_CaMap.put(time, temp);}}
                                    else if (temp.getType().equals("Inorganic phosphorus")){if(!PhosphorusMap.containsKey(time)){PhosphorusMap.put(time,temp);}}
                                    else if (temp.getType().equals("Magnesium")){if(!MagnesiumMap.containsKey(time)){MagnesiumMap.put(time,temp);}}
                                    else if (temp.getType().equals("Glucose") || temp.getType().equals("(POCT) Blood glucose test") || temp.getType().equals("Blood glucose test")) {if (!GlucoseMap.containsKey(time)) {GlucoseMap.put(time, temp);}}
                                    else if (temp.getType().equals("HbA1c-NGSP")){if(!HbA1cMap.containsKey(time)){HbA1cMap.put(time,temp);}}
                                    else if (temp.getType().equals("Lactate")){if(!LacticMap.containsKey(time)){LacticMap.put(time,temp);}}
                                }
                            }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
//            Comparator<DiagnosisDTO> dateCompare = Comparator.comparing(DiagnosisDTO::getTime).reversed();

                // 진단 항목별로 데이터 생성
//                LymphocyteMap = Lymphocyte.stream().collect(Collectors.toMap(l -> l.getTime(), l -> l));
//                NeutMap = Neut.stream().collect(Collectors.toMap(l -> l.getTime(), l -> l));
//                Platelet_countMap = Platelet_count.stream().collect(Collectors.toMap(l -> l.getTime(), l -> l));
//                proteinMap = protein.stream().collect(Collectors.toMap(l -> l.getTime(), l -> l));
//                WBC_countMap = WBC_count.stream().collect(Collectors.toMap(l -> l.getTime(), l -> l));
                // 존재하는 모든 날짜 정렬 작업
                Collections.sort(dateColumn);

//  STEP 2 : 고신 포맷에 맞춰 엑셀 생성 진행
                try {
                    File file = new File(resultPath + filename);
                    FileOutputStream fileout = new FileOutputStream(file);

                    XSSFWorkbook workbook = new XSSFWorkbook();
                    XSSFSheet sheet = workbook.createSheet(filename);
                    // 칼럼 설정
                    int preIndex = 4;
                    String[] columns = new String[preIndex + dateColumn.size()];
                    log.info("this is column size: " + columns.length);
                    columns[0] = "검사명";
                    columns[1] = "단위";
                    columns[2] = "하한";
                    columns[3] = "상한";

                    int dateIndex = 0;
                    for (int i = 4; i < columns.length; i++) {
                        columns[i] = String.valueOf(dateColumn.get(dateIndex));
                        dateIndex++;
                    }

                    Row header = sheet.createRow(0);
                    for (int col = 0; col < columns.length; col++) {
                        Cell cell = header.createCell(col);
                        cell.setCellValue(columns[col]);
                    }

                    int rowIndex = 1;
                    int colIndex = 0;
                    for (String set : preset) {
                        Row row = sheet.createRow(rowIndex);
                        row.createCell(0).setCellValue(set);
                        if (set.equals("PT(%)")) {
                            row.createCell(1).setCellValue(INR[0]);
                        } else if (set.equals("Bilirubin total")) {
                            row.createCell(1).setCellValue(TB[0]);
                        } else {
                            row.createCell(1).setCellValue("");
                        }
                        row.createCell(2).setCellValue("");
                        if (set.equals("PT(%)")) {
                            row.createCell(3).setCellValue(INR[1]);
                        } else if (set.equals("Bilirubin total")) {
                            row.createCell(3).setCellValue(TB[1]);
                        } else {
                            row.createCell(3).setCellValue("");
                        }

//화순 ver
//        ("WBC count", "Neutrophil", "Lymphocyte", "Platelet", "PT(INR)", "PT(%)", "protein", "Albumin",
//          "Bilirubin total", "Direct Bilirubin", "AST", "ALT", "Cr(Creatinine)", "CRP",
//          "HBs Ag", "Anti-HBs", "Anti-HBc", "Anti-HCV", "AFP (CLIA)", "PIVKA-2", "CEA", "CA 19-9", "ELF", "ICG R15",
//          "Hgb", "MCV", "Rh typing", "ABO cell typing", "eGFR(MDRD)", "monocyte", "Eosinophil", "ALP(Alk Phosphatase)",
//          "LDH", "r-GTP", "APTT", "Cholesterol total", "Triglyceride", "HDL-Cholesterol", "LDL-Cholesterol", "Calculated LDL-C",
//          "Amylase Total", "Lipase", "BUN", "Sodium(Na)", "Potassium(K)", "Chloride(Cl)", "Ionized Ca", "Corrected Ca",
//          "Phosphorus(P)", "Magnesium(Mg)", "Glucose", "Hb A1c (IFCC)", "Lactic Acid (BGA)")

//        ("WBC count", "Neutrophil", "Lymphocyte", "Platelet Count", "PT(INR)", "PT(%)", "Total protein", "Albumin",
//                "Bilirubin total", "Direct Bilirubin", "AST", "ALT", "Cr(Creatinine)", "HS-CRP",
//                "HBs Ag", "Anti-HBs", "Anti-HBc", "Anti-HCV", "AFP (CLIA)", "PIVKA-2", "CEA", "CA 19-9", "ELF", "ICG R15",
//                "Hgb", "MCV", "Rh typing", "ABO cell typing", "eGFR(MDRD)", "monocyte", "Eosinophil", "ALP(Alk Phosphatase)",
//                "LDH", "r-GTP", "APTT", "Cholesterol total", "Triglyceride", "HDL-Cholesterol", "LDL-Cholesterol", "Calculated LDL-C",
//                "Amylase Total", "Lipase", "BUN", "Sodium(Na)", "Potassium(K)", "Chloride(Cl)", "Ionized Ca", "Corrected Ca",
//                "Phosphorus(P)", "Magnesium(Mg)", "Glucose", "Hb A1c (IFCC)", "Lactic Acid (BGA)")

                        for (int i = 0; i < dateColumn.size(); i++) {
//        ("WBC count", "Neutrophil", "Lymphocyte", "Platelet Count", "PT(INR)", "PT(%)", "Total protein", "Albumin",
                            if (set.equals("WBC count")) {
                                if (dateColumn.get(i) != null)
                                    if (WBC_countMap.get(dateColumn.get(i)) != null)
                                        row.createCell(preIndex + i).setCellValue(WBC_countMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Neutrophil")) {
                                if(dateColumn.get(i) != null)
                                    if(NeutrophilMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(NeutrophilMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Lymphocyte")) {
                                if(dateColumn.get(i) != null)
                                    if(LymphocyteMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(LymphocyteMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Platelet Count")) {
                                if(dateColumn.get(i) != null)
                                    if(Platelet_countMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Platelet_countMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("PT(INR)")) {
                                if(dateColumn.get(i) != null)
                                    if(PT_INRMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(PT_INRMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            }  else if (set.equals("PT(%)")) {
                                if(dateColumn.get(i) != null)
                                    if(PTMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(PTMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Total protein")) {
                                if(dateColumn.get(i) != null)
                                    if(proteinMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(proteinMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Albumin")) {
                                if(dateColumn.get(i) != null)
                                    if(AlbuminMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(AlbuminMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Protein")) {
                                if(dateColumn.get(i) != null)
                                    if(proteinMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(proteinMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
//                "Bilirubin total", "Direct Bilirubin", "AST", "ALT", "Cr(Creatinine)", "HS-CRP",
                            } else if (set.equals("Bilirubin total")) {
                                if(dateColumn.get(i) != null)
                                    if(Bilirubin_totalMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Bilirubin_totalMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Direct Bilirubin")) {
                                if(dateColumn.get(i) != null)
                                    if(Direct_BilirubinMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Direct_BilirubinMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("AST")) {
                                if(dateColumn.get(i) != null)
                                    if(ASTMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(ASTMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("ALT")) {
                                if(dateColumn.get(i) != null)
                                    if(ALTMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(ALTMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Cr(Creatinine)")) {
                                if(dateColumn.get(i) != null)
                                    if(CreatinineMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(CreatinineMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("HS-CRP")) {
                                if(dateColumn.get(i) != null)
                                    if(HS_CRPMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(HS_CRPMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            }

//          "HBs Ag", "Anti-HBs", "Anti-HBc", "Anti-HCV", "AFP (CLIA)", "PIVKA-2", "CEA", "CA 19-9", "ELF", "ICG R15",
                              else if (set.equals("HBs Ag")) {
                                if(dateColumn.get(i) != null)
                                    if(HBs_AgMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(HBs_AgMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Anti-HBs")) {
                                if(dateColumn.get(i) != null)
                                    if(Anti_HBsMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Anti_HBsMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Anti-HBc")) {
                                if(dateColumn.get(i) != null)
                                    if(Anti_HBcMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Anti_HBcMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Anti-HCV")) {
                                if(dateColumn.get(i) != null)
                                    if(Anti_HCVMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Anti_HCVMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("AFP (CLIA)")) {
                                if(dateColumn.get(i) != null)
                                    if(AFP_CLIAMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(AFP_CLIAMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("PIVKA-2")) {
                                if(dateColumn.get(i) != null)
                                    if(PIVKA_2Map.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(PIVKA_2Map.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("CEA")) {
                                if(dateColumn.get(i) != null)
                                    if(CEAMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(CEAMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("CA 19-9")) {
                                if(dateColumn.get(i) != null)
                                    if(CA_19Map.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(CA_19Map.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("ELF")) {
                                if(dateColumn.get(i) != null)
                                    if(ELFMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(ELFMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("ICG R15")) {
                                if(dateColumn.get(i) != null)
                                    if(ICG_R15Map.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(ICG_R15Map.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            }
//          "Hgb", "MCV", "Rh typing", "ABO cell typing", "eGFR(MDRD)", "monocyte", "Eosinophil", "ALP(Alk Phosphatase)",
                            else if (set.equals("Hgb")) {
                                if(dateColumn.get(i) != null)
                                    if(HgbMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(HgbMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("MCV")) {
                                if(dateColumn.get(i) != null)
                                    if(MCVMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(MCVMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Rh typing")) {
                                if(dateColumn.get(i) != null)
                                    if(Rh_typingMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Rh_typingMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("ABO cell typing")) {
                                if(dateColumn.get(i) != null)
                                    if(ABO_cellMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(ABO_cellMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("eGFR(MDRD)")) {
                                if(dateColumn.get(i) != null)
                                    if(eGFRMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(eGFRMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("monocyte")) {
                                if(dateColumn.get(i) != null)
                                    if(monocyteMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(monocyteMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Eosinophil")) {
                                if(dateColumn.get(i) != null)
                                    if(EosinophilMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(EosinophilMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("ALP(Alk Phosphatase)")) {
                                if(dateColumn.get(i) != null)
                                    if(ALPMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(ALPMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            }
//          "LDH", "r-GTP", "APTT", "Cholesterol total", "Triglyceride", "HDL-Cholesterol", "LDL-Cholesterol", "Calculated LDL-C",
                            else if (set.equals("LDH")) {
                                if(dateColumn.get(i) != null)
                                    if(LDHMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(LDHMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("r-GTP")) {
                                if(dateColumn.get(i) != null)
                                    if(r_GTPMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(r_GTPMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("APTT")) {
                                if(dateColumn.get(i) != null)
                                    if(APTTMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(APTTMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Cholesterol total")) {
                                if(dateColumn.get(i) != null)
                                    if(Cholesterol_totalMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Cholesterol_totalMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Triglyceride")) {
                                if(dateColumn.get(i) != null)
                                    if(TriglycerideMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(TriglycerideMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("HDL-Cholesterol")) {
                                if(dateColumn.get(i) != null)
                                    if(HDLMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(HDLMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("LDL-Cholesterol")) {
                                if(dateColumn.get(i) != null)
                                    if(LDLMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(LDLMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Calculated LDL-C")) {
                                if(dateColumn.get(i) != null)
                                    if(LDL_CMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(LDL_CMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            }
//          "Amylase Total", "Lipase", "BUN", "Sodium(Na)", "Potassium(K)", "Chloride(Cl)", "Ionized Ca", "Corrected Ca",
                            else if (set.equals("Amylase Total")) {
                                if(dateColumn.get(i) != null)
                                    if(AmylaseMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(AmylaseMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Lipase")) {
                                if(dateColumn.get(i) != null)
                                    if(LipaseMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(LipaseMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("BUN")) {
                                if(dateColumn.get(i) != null)
                                    if(BUNMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(BUNMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Sodium(Na)")) {
                                if(dateColumn.get(i) != null)
                                    if(SodiumMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(SodiumMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Potassium(K)")) {
                                if(dateColumn.get(i) != null)
                                    if(PotassiumMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(PotassiumMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Chloride(Cl)")) {
                                if(dateColumn.get(i) != null)
                                    if(ChlorideMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(ChlorideMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Ionized Ca")) {
                                if(dateColumn.get(i) != null)
                                    if(Ionized_CaMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Ionized_CaMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Corrected Ca")) {
                                if(dateColumn.get(i) != null)
                                    if(Corrected_CaMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(Corrected_CaMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            }
//          "Phosphorus(P)", "Magnesium(Mg)", "Glucose", "Hb A1c (IFCC)", "Lactic Acid (BGA)")
                            else if (set.equals("Phosphorus(P)")) {
                                if(dateColumn.get(i) != null)
                                    if(PhosphorusMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(PhosphorusMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Magnesium(Mg)")) {
                                if(dateColumn.get(i) != null)
                                    if(MagnesiumMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(MagnesiumMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Glucose")) {
                                if(dateColumn.get(i) != null)
                                    if(GlucoseMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(GlucoseMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Hb A1c (IFCC)")) {
                                if(dateColumn.get(i) != null)
                                    if(HbA1cMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(HbA1cMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            } else if (set.equals("Lactic Acid (BGA)")) {
                                if(dateColumn.get(i) != null)
                                    if(LacticMap.get(dateColumn.get(i))!=null)
                                        row.createCell(preIndex + i).setCellValue(LacticMap.get(dateColumn.get(i)).getResult());
                                    else row.createCell(preIndex + i).setCellValue("");
                            }
                            else {
                                row.createCell(preIndex + i).setCellValue("");
                            }
                    }
                    rowIndex++;
                }

                    workbook.write(fileout);
                    fileout.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }

}

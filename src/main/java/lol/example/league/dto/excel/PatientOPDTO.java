package lol.example.league.dto.excel;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class PatientOPDTO {

    @Builder
    @Getter
    public static class OPdate {
        private LocalDate date;
        private Long patientId;
        private String fileName;
    }

    @Builder
    @Getter
    public static class PatientData {
        private LocalDateTime date;
        private Double data;
        private String data2;
    }


    @Builder
    @Getter
    public static class ISGLS {
        private String fileName;
        private Long patientId;
        private LocalDate opDate;
        private Double maxPT;
        private Double maxBil;
        private LocalDateTime prePTDate;
        private LocalDateTime preBILDate;
        private Double prePT;
        private Double preBil;
        private LocalDate checkdate;
        private Double checkPT;
        private Double checkBil;
        private Boolean result;
    }

    @Builder
    @Data
    public static class PatientOP {
        private Double wbc;
        private Double Neutrophil;
        private Double lymphocyte;
        private Double Platelet;
        private Double PT_INR;
        private Double PT_P;
        private Double protein;
        private Double Albumin;
        private Double TB;
        private Double DB;
        private Double AST;
        private Double ALT;
        private Double Cr;
        private Double HS_CRP;

        private String HbsAg;
        private String HbcAb;
        private String AntiHbs;
        private String AntiHCV;
        private String AFP;
        private String PIVKA;
        private String CEA;
        private String CA19;
        private String ELF;
        private String IGSR15;

    }

    @Builder
    @Getter
    @Setter
    public static class PatientPODData {
        private List<PatientData> wbc;
        private List<PatientData> Neutrophil;
        private List<PatientData> lymphocyte;
        private List<PatientData> Platelet;
        private List<PatientData> PT_INR;
        private List<PatientData> PT_P;
        private List<PatientData> protein;
        private List<PatientData> Albumin;
        private List<PatientData> TB;
        private List<PatientData> DB;
        private List<PatientData> AST;
        private List<PatientData> ALT;
        private List<PatientData> Cr;
        private List<PatientData> HS_CRP;


        public static PatientPODData create (){
            return PatientPODData.builder()
                    .wbc(new ArrayList<>())
                    .Neutrophil(new ArrayList<>())
                    .lymphocyte(new ArrayList<>())
                    .Platelet(new ArrayList<>())
                    .PT_INR(new ArrayList<>())
                    .PT_P(new ArrayList<>())
                    .protein(new ArrayList<>())
                    .Albumin(new ArrayList<>())
                    .TB(new ArrayList<>())
                    .DB(new ArrayList<>())
                    .AST(new ArrayList<>())
                    .ALT(new ArrayList<>())
                    .Cr(new ArrayList<>())
                    .HS_CRP(new ArrayList<>())
                    .build();
        }
    }

    @Builder
    @Data
    public static class PODSet {
        private Double POD1;
        private Double POD3;
        private Double POD5;
        private Double POD7;
        private Double POD10;
        private Double POD14;
    }



    @Builder
    @Data
    public static class PatientPODResult {
        private String patientId;
        private LocalDate opDate;

        private PatientOP preop;
        private PODSet wbc;
        private PODSet Neutrophil;
        private PODSet lymphocyte;
        private PODSet Platelet;
        private PODSet PT_INR;
        private PODSet PT_P;
        private PODSet protein;
        private PODSet Albumin;
        private PODSet TB;
        private PODSet DB;
        private PODSet AST;
        private PODSet ALT;
        private PODSet Cr;
        private PODSet HS_CRP;


    }

    @Builder
    @Data
    public static class PatientExtraPreOp {
        private String patientId;
        private LocalDate opDate;
        private String Hgb;
        private String MCV;
        private String Rh;
        private String ABO;
        private String MDRD;
        private String Monocyte;
        private String Eosinophil;
        private String ALP;
        private String LDH;
        private String GTP;
        private String aPTT;
        private String TotalCho;
        private String Trigly;
        private String HDL;
        private String LDL;
        private String LDLc;
        private String Amylase;
        private String Lipase;
        private String BUN;
        private String sodium;
        private String Potassium;
        private String Chloride;
        private String ICalcium;
        private String TCalcium;
        private String Inorganic;
        private String Magnesium;
        private String Glucose;
        private String HBA1c;
        private String Lactate;
        private String AFP;
        private String PIVKA2;
        private String CEA;
        private String CA19;
        private String ELF;
        private String ICGR15;

    }

    @Builder
    @Getter
    @Setter
    public static class PatientExtraPODData {
        private List<PatientData> Hgb;
        private List<PatientData> MCV;
        private List<PatientData> Rh;
        private List<PatientData> ABO;
        private List<PatientData> MDRD;
        private List<PatientData> Monocyte;
        private List<PatientData> Eosinophil;
        private List<PatientData> ALP;
        private List<PatientData> LDH;
        private List<PatientData> GTP;
        private List<PatientData> aPTT;
        private List<PatientData> TotalCho;
        private List<PatientData> Trigly;
        private List<PatientData> HDL;
        private List<PatientData> LDL;
        private List<PatientData> LDLc;
        private List<PatientData> Amylase;
        private List<PatientData> Lipase;
        private List<PatientData> BUN;
        private List<PatientData> sodium;
        private List<PatientData> Potassium;
        private List<PatientData> Chloride;
        private List<PatientData> ICalcium;
        private List<PatientData> TCalcium;
        private List<PatientData> Inorganic;
        private List<PatientData> Magnesium;
        private List<PatientData> Glucose;
        private List<PatientData> HBA1c;
        private List<PatientData> Lactate;

        public static PatientExtraPODData create (){
            return PatientExtraPODData.builder()
                    .Hgb(new ArrayList<>())
                    .MCV(new ArrayList<>())
                    .Rh(new ArrayList<>())
                    .ABO(new ArrayList<>())
                    .MDRD(new ArrayList<>())
                    .Monocyte(new ArrayList<>())
                    .Eosinophil(new ArrayList<>())
                    .ALP(new ArrayList<>())
                    .LDH(new ArrayList<>())
                    .GTP(new ArrayList<>())
                    .aPTT(new ArrayList<>())
                    .TotalCho(new ArrayList<>())
                    .Trigly(new ArrayList<>())
                    .HDL(new ArrayList<>())
                    .LDL(new ArrayList<>())
                    .LDLc(new ArrayList<>())
                    .Amylase(new ArrayList<>())
                    .Lipase(new ArrayList<>())
                    .BUN(new ArrayList<>())
                    .sodium(new ArrayList<>())
                    .Potassium(new ArrayList<>())
                    .Chloride(new ArrayList<>())
                    .ICalcium(new ArrayList<>())
                    .TCalcium(new ArrayList<>())
                    .Inorganic(new ArrayList<>())
                    .Magnesium(new ArrayList<>())
                    .Glucose(new ArrayList<>())
                    .HBA1c(new ArrayList<>())
                    .Lactate(new ArrayList<>())
                    .build();
        }
    }

    @Builder
    @Data
    public static class ExtraPODSet {
        private String POD1;
        private String POD3;
        private String POD5;
        private String POD7;
        private String POD10;
        private String POD14;
    }



    @Builder
    @Data
    public static class PatientExtraPODResult {
        private String patientId;
        private LocalDate opDate;

        private PatientExtraPreOp preop;
        private ExtraPODSet Hgb;
        private ExtraPODSet MCV;
        private ExtraPODSet Rh;
        private ExtraPODSet ABO;
        private ExtraPODSet MDRD;
        private ExtraPODSet Monocyte;
        private ExtraPODSet Eosinophil;
        private ExtraPODSet ALP;
        private ExtraPODSet LDH;
        private ExtraPODSet GTP;
        private ExtraPODSet aPTT;
        private ExtraPODSet TotalCho;
        private ExtraPODSet Trigly;
        private ExtraPODSet HDL;
        private ExtraPODSet LDL;
        private ExtraPODSet LDLc;
        private ExtraPODSet Amylase;
        private ExtraPODSet Lipase;
        private ExtraPODSet BUN;
        private ExtraPODSet sodium;
        private ExtraPODSet Potassium;
        private ExtraPODSet Chloride;
        private ExtraPODSet ICalcium;
        private ExtraPODSet TCalcium;
        private ExtraPODSet Inorganic;
        private ExtraPODSet Magnesium;
        private ExtraPODSet Glucose;
        private ExtraPODSet HBA1c;
        private ExtraPODSet Lactate;


    }

}

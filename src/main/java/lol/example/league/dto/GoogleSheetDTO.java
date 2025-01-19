package lol.example.league.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GoogleSheetDTO {

    private String sheetName;

    private String sheetId;

    private List<List<Object>> dataToBeUpdated;

    private List<String> emails;

}

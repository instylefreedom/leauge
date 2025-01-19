package lol.example.league.dto.request;

import lol.example.league.entity.board.BoardType;
import lombok.Getter;

public class BoardRequest {

    @Getter
    public static class create{
        private String title;
        private String type;
        private String content;
    }

    @Getter
    public static class update{
        private Long boardId;
        private String title;
        private String content;
    }

}

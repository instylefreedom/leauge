package lol.example.league.dto.request;

import lombok.Getter;

public class CommentRequest {

    @Getter
    public static class body{
        private Long commentId;
        private String content;
    }

    @Getter
    public static class create{
        private Long boardId;
        private String content;
    }
}

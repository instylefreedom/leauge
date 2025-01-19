package lol.example.league.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lol.example.league.entity.board.BoardType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public class BoardResponse {

    @Getter
    @Builder
    public static class Body{

        private Long boardId;
        private String title;
        private BoardType type;
        private String content;
        private LocalDateTime createdAt;
        private Long createdBy;
        private Long like;
        private Long selfLike;
        private String userName;
        private String profilePic;
        private Long commentCnt;

    }

    @Getter
    @Builder
    public static class Title{

        private int num;
        private Long boardId;
        private String title;
        private LocalDateTime createdAt;
        private Long createdBy;
        private Long like;

    }

    @Getter
    @Builder
    public static class Like {

        private int stat;
        private Long like;
    }
}

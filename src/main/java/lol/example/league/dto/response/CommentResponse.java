package lol.example.league.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

public class CommentResponse {


    @Getter
    @Builder
    @AllArgsConstructor
    public static class Body{
        private Long commentId;
        private Long boardId;
        private Long parentId;
        private Long userId;
        private String content;
        private LocalDateTime createdAt;
        private Long createdBy;
        private Long like;
        private Long selfLike;
        private String userName;
        private String profilePic;
    }

    @Getter
    @Builder
    public static class Like {

        private int stat;
        private Long like;
    }
}

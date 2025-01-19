package lol.example.league.entity.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardType {

    ADMIN("ADMIN", "관리자 게시판"),
    FREE("FREE", "자유 게시판");

    private final String key;
    private final String title;
}

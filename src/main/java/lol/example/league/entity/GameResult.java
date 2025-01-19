package lol.example.league.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GameResult {

    WIN("WIN", "승리"),
    LOSE("LOSE", "패배");

    private final String key;
    private final String title;

}

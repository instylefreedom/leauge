package lol.example.league.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class UserNameResponse {
    private String name;

    public UserNameResponse(String name){
        this.name = name;
    }
}

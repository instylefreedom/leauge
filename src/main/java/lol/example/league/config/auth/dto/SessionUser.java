package lol.example.league.config.auth.dto;

import lol.example.league.entity.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String pricture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.pricture = user.getPicture();
    }
}

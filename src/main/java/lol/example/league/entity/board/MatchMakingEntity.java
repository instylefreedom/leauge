package lol.example.league.entity.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;

@Entity
@Table(name = "matchmaking")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MatchMakingEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "team")
    private String team;

    @Column(name = "user_name",unique = true)
    private String userName;

    @Column(name = "tier")
    private String tier;

    @Column(name = "bracket")
    private Integer bracket;

    @Column(name = "rating")
    private Long rating;

    @Column(name = "main_lane")
    private String mainLane;

    @Column(name = "sub_lane")
    private String subLane;
}

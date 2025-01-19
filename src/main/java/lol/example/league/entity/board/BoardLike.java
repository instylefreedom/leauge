package lol.example.league.entity.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_like")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardLike {

    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @Column
    private Long boardId;

    @Column
    private long userId;
}

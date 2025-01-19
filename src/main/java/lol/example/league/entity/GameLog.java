package lol.example.league.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_log")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameLog {

    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(name = "play_date")
    private Integer playDate;

    @Column(name = "season")
    private Integer season;

    @Column(name = "round")
    private Integer round;

    @Column(name = "set")
    private Integer set;

    @Enumerated(EnumType.STRING)
    @Column
    private GameResult result;

    @Column(name = "team")
    private Integer team;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "game_id")
    private Integer gameId;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;
}

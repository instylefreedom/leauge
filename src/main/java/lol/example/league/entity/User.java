package lol.example.league.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = "user_name",unique = true)
    private String userName;

    @Column(name = "tier")
    private Integer tier;

    @Column(name = "bracket")
    private Integer bracket;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "main_lane")
    private String mainLane;

    @Column(name = "sub_lane")
    private String subLane;

    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

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


    public void update(String userName, String mainLane, String subLane, Long updatedBy, LocalDateTime updatedAt){
        this.userName = userName;
        this.mainLane = mainLane;
        this.subLane = subLane;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public void updateRating(Integer rating){
        this.rating = rating;
    }

    public User update(String name, String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }

    @Builder
    public User(String name, String email, String picture, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    public void updateTier(Integer tier, Integer bracket){
        this.tier = tier;
        this.bracket = bracket;
    }

}

package com.example.tracky.user.friends;

import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "friend_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_friend_from_to", // 제약 조건 이름
                        columnNames = {"fromUser_id", "toUser_id"} // 유니크해야 할 컬럼 조합
                )
        })
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 친구 요청 보낸 유저
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User fromUser;

    // 친구 요청 받은 유저
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User toUser;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Friend(User fromUser, User toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    protected Friend() {
    }
}

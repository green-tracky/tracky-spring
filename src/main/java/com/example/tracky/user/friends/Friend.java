package com.example.tracky.user.friends;

import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "friend_tb")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 친구 요청 보낸 유저
    @ManyToOne(fetch = FetchType.LAZY)
    private User fromUser;

    // 친구 요청 받은 유저
    @ManyToOne(fetch = FetchType.LAZY)
    private User toUser;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public Friend(User fromUser, User toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }
}

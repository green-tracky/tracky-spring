package com.example.tracky.user.friends.friendinvite;

import com.example.tracky.user.User;
import com.example.tracky.user.friends.friendinvite.enums.InviteStatusType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "friend_invite_tb")
public class FriendInvite {

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

    @Enumerated(EnumType.STRING)
    InviteStatusType status = InviteStatusType.WAITING;

    private LocalDateTime responsedAt;

    public FriendInvite(User fromUser, User toUser, LocalDateTime createdAt, InviteStatusType status, LocalDateTime responsedAt) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.createdAt = createdAt;
        this.status = status;
        this.responsedAt = responsedAt;
    }
}

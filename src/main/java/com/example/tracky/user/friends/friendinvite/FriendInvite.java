package com.example.tracky.user.friends.friendinvite;

import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import static com.example.tracky._core.enums.ErrorCodeEnum.INVALID_INVITE_RESPONSE_STATE;


@Getter
@Entity
@Table(name = "friend_invite_tb")
public class FriendInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 친구 요청 보낸 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User fromUser;

    // 친구 요청 받은 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User toUser;

    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Enumerated(EnumType.STRING)
    private InviteStatusEnum status;

    @UpdateTimestamp
    private LocalDateTime respondedAt;

    @Builder
    public FriendInvite(Integer id, User fromUser, User toUser, InviteStatusEnum status) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.status = status;
    }

    protected FriendInvite() {
    }

    public void accept() {
        if (this.status != InviteStatusEnum.PENDING) {
            throw new ExceptionApi400(INVALID_INVITE_RESPONSE_STATE);
        }
        this.status = InviteStatusEnum.ACCEPTED;
    }

    public void reject() {
        if (this.status != InviteStatusEnum.PENDING) {
            throw new ExceptionApi400(INVALID_INVITE_RESPONSE_STATE);
        }
        this.status = InviteStatusEnum.REJECTED;
    }
}

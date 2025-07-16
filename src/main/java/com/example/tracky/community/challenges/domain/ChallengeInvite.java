package com.example.tracky.community.challenges.domain;

import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.error.ex.ExceptionApi400;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static com.example.tracky._core.enums.ErrorCodeEnum.INVALID_INVITE_RESPONSE_STATE;

@Getter
@Table(
        name = "challenge_invite_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_challenge_invite",
                        columnNames = {"fromUser_id", "toUser_id", "challenge_id"}
                )
        })
@Entity
public class ChallengeInvite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InviteStatusEnum status; // 대기/수락/거절

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User fromUser; // 초대요청을 보낸 유저

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User toUser; // 초대요청을 받는 유저

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Challenge challenge; // 초대대상 챌린지

    @CreationTimestamp
    private LocalDateTime createdAt; // 요청 시간

    private LocalDateTime responseAt; // 응답 시간

    @Builder
    public ChallengeInvite(Integer id, InviteStatusEnum status, User fromUser, User toUser, Challenge challenge) {
        this.id = id;
        this.status = status;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.challenge = challenge;
    }

    protected ChallengeInvite() {
    }

    public void accept() {
        if (this.status != InviteStatusEnum.PENDING) {
            throw new ExceptionApi400(INVALID_INVITE_RESPONSE_STATE);
        }
        this.status = InviteStatusEnum.ACCEPTED;
        this.responseAt = LocalDateTime.now();
    }

    public void reject() {
        if (this.status != InviteStatusEnum.PENDING) {
            throw new ExceptionApi400(INVALID_INVITE_RESPONSE_STATE);
        }
        this.status = InviteStatusEnum.REJECTED;
        this.responseAt = LocalDateTime.now();
    }
}

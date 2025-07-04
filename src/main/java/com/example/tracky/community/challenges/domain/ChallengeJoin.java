package com.example.tracky.community.challenges.domain;

import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Table(
        name = "challenge_join_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_challenge_join_user_challenge",
                        columnNames = {"user_id", "challenge_id"}
                )
        }
)
@Entity
public class ChallengeJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user; // 챌린지에 참가한 유저

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Challenge challenge; // 챌린지

    @CreationTimestamp
    private LocalDateTime joinDate;

    @Builder
    public ChallengeJoin(Integer id, User user, Challenge challenge) {
        this.id = id;
        this.user = user;
        this.challenge = challenge;
    }

    protected ChallengeJoin() {
    }

}
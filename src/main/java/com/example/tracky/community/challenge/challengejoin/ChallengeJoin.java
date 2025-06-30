package com.example.tracky.community.challenge.challengejoin;

import com.example.tracky.community.challenge.Challenge;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Table(name = "challenge_join_tb")
@Entity
public class ChallengeJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Challenge challenge;

    @CreationTimestamp
    private LocalDateTime joinDate;

    @Builder
    public ChallengeJoin(Integer id, User user, Challenge challenge) {
        this.id = id;
        this.user = user;
        this.challenge = challenge;
    }
}
package com.example.tracky.community.challenge.challengejoin;

import com.example.tracky.community.challenge.Challenge;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Table(name = "user_challenge_tb")
@Entity
public class ChallengeJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Challenge challenge;

    @CreationTimestamp
    private LocalDateTime joinDate;

    public ChallengeJoin(Integer id, User user, Challenge challenge) {
        this.id = id;
        this.user = user;
        this.challenge = challenge;
    }
}
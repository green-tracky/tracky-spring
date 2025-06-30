package com.example.tracky.community.challenge.challengejoin;

import com.example.tracky.community.challenge.Challenge;
import com.example.tracky.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "user_challenge_tb")
public class ChallengeJoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Challenge challenge;
}
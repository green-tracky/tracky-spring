package com.example.tracky.community.challenge;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChallengeStatusEnum {
    INPROGRESS("진행중"),
    COMPLETED("종료");

    private final String label;
}

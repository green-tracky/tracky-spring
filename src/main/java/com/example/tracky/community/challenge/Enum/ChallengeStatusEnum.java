package com.example.tracky.community.challenge.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChallengeStatusEnum {
    INPROGRESS("진행중"),
    COMPLETED("종료");

    private final String label;

    public static ChallengeStatusEnum fromLabel(String label) {
        for (ChallengeStatusEnum status : values()) {
            if (status.label.equals(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }
}

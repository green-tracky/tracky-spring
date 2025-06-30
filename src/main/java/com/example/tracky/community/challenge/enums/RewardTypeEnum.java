package com.example.tracky.community.challenge.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사설 챌린지에서 사용될 고정된 리워드의 종류를 정의하는 Enum 클래스입니다.
 * 이 Enum은 reward_master_tb의 reward_type 컬럼과 매핑됩니다.
 */
@Getter
@RequiredArgsConstructor
public enum RewardTypeEnum {

    GOLD("금메달", "https://example.com/rewards/gold_medal.png"),
    SILVER("은메달", "https://example.com/rewards/silver_medal.png"),
    BRONZE("동메달", "https://example.com/rewards/bronze_medal.png"),
    PARTICIPATION("참여상", "https://example.com/rewards/completion_medal.png");

    private final String name;
    private final String imageUrl;
}

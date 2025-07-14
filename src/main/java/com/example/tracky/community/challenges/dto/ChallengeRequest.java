package com.example.tracky.community.challenges.dto;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.PeriodTypeEnum;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class ChallengeRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "챌린지 이름은 필수 입력 항목입니다.")
        @Size(max = 50, message = "챌린지 이름은 50자를 초과할 수 없습니다.")
        private String name;

        @NotNull(message = "이미지 index 필수 입력 항목입니다.")
        @Positive(message = "목표 거리는 0보다 커야 합니다.")
        private Integer imgIndex;

        @NotNull(message = "목표 거리는 필수 입력 항목입니다.")
        @Positive(message = "목표 거리는 0보다 커야 합니다.")
        private Integer targetDistance; // 거리 m 단위

        @NotNull(message = "시작일은 필수 입력 항목입니다.")
        private LocalDateTime startDate;

        @NotNull(message = "종료일은 필수 입력 항목입니다.")
        private LocalDateTime endDate;

        public Challenge toEntity(User user) {
            return Challenge.builder()
                    .name(name)
                    .imgIndex(imgIndex)
                    .startDate(startDate)
                    .endDate(endDate)
                    .targetDistance(targetDistance)
                    .isInProgress(true)
                    .type(ChallengeTypeEnum.PRIVATE)
                    .periodType(PeriodTypeEnum.ETC)
                    .creator(user)
                    .build();
        }
    }

    @Data
    public static class UpdateDTO {
        @NotBlank(message = "챌린지 이름은 필수 입력 항목입니다.")
        @Size(max = 50, message = "챌린지 이름은 50자를 초과할 수 없습니다.")
        private String name;
    }
}

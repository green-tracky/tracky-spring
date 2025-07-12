package com.example.tracky.community.challenges.dto;

import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.PeriodTypeEnum;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.user.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

public class ChallengeRequest {

    @Data
    public static class SaveDTO {
        @NotBlank(message = "이름은 필수 입력값입니다")
        @Size(max = 20, message = "제목은 최대 20자까지 입력할 수 있습니다")
        private String name;
        private String imageUrl;
        @NotNull(message = "목표 거리는 필수 입력값입니다")
        @Min(value = 1, message = "목표 거리는 1 이상이어야 합니다")
        private Integer targetDistance; // 거리 m 단위
        @NotNull(message = "시작일은 필수 입력값입니다")
        private LocalDateTime startDate;
        @NotNull(message = "종료일은 필수 입력값입니다")
        private LocalDateTime endDate;

        public Challenge toEntity(User user) {
            return Challenge.builder()
                    .name(name)
                    .imageUrl(imageUrl)
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
}

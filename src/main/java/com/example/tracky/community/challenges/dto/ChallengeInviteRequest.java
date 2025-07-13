package com.example.tracky.community.challenges.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;


public class ChallengeInviteRequest {

    @Data
    public static class InviteRequestDTO {
        @NotEmpty(message = "초대할 친구를 한 명 이상 선택해주세요.")
        private List<@NotNull(message = "친구 ID는 null일 수 없습니다.")
        @Positive(message = "친구 ID는 유효한 양수여야 합니다.") Integer> friendIds;
    }
}

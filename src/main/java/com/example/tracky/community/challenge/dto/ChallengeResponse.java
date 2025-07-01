package com.example.tracky.community.challenge.dto;

import com.example.tracky.community.challenge.domain.Challenge;
import com.example.tracky.community.challenge.domain.PublicChallenge;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChallengeResponse {

    // 최종 응답을 감싸는 메인 DTO
    @Data
    public static class ListDTO {
        private RecommendedDTO recommendedChallenge; // 추천 챌린지. 공개 챌린지 중 랜덤으로 하나
        private List<MyChallengeDTO> myChallenges; // 내가 참가하고 있는 챌린지 목록
        private List<JoinableDTO> joinableChallenges; // 참가할 수 있는 공개 챌린지 목록
        private List<PastChallengeDTO> pastChallenges; // 내가 참여했던 챌린지 목록

        public ListDTO(RecommendedDTO recommendedChallenge, List<MyChallengeDTO> myChallenges, List<JoinableDTO> joinableChallenges, List<PastChallengeDTO> pastChallenges) {
            this.recommendedChallenge = recommendedChallenge;
            this.myChallenges = myChallenges;
            this.joinableChallenges = joinableChallenges;
            this.pastChallenges = pastChallenges;
        }
    }

    // 추천 챌린지 DTO
    @Data
    public static class RecommendedDTO {
        private Integer id;
        private String title; // 이미지 없을 때 식별용
        private String imageUrl; // 이미지
        private Integer participantCount; // 참가자 수

        // PublicChallenge 엔티티와 참가자 수를 받아 필드를 초기화합니다.
        public RecommendedDTO(PublicChallenge challenge, Integer participantCount) {
            this.id = challenge.getId();
            this.title = formatTitle(challenge.getTargetDistance());
            this.imageUrl = challenge.getRewardImageUrl();
            this.participantCount = participantCount;
        }
    }

    // 나의 챌린지 DTO
    @Data
    public static class MyChallengeDTO {
        private Integer id;
        private String title; //
        private String name;
        private Integer myDistance;
        private Integer targetDistance;
        private Boolean isInProgress;

        private

        // Challenge 엔티티와 달성 거리를 받아 필드를 초기화합니다.
        public MyChallengeDTO(Challenge challenge, Double achievedDistance) {
            this.id = challenge.getId();
            this.title = formatTitle(challenge.getTargetDistance());
            this.name = challenge.getName();
            this.progress = String.format("%.2f / %.2fkm", achievedDistance / 1000.0, challenge.getTargetDistance() / 1000.0);

            boolean isEndingSoon = challenge.getEndDate().isBefore(LocalDateTime.now().plusDays(1));
            this.status = isEndingSoon && challenge.getEndDate().isAfter(LocalDateTime.now())
                    ? "종료: 결과 대기 중"
                    : formatTimeLeft(challenge.getEndDate());
        }
    }

    // 참여하기 DTO
    @Data
    public static class JoinableDTO {
        private Integer id;
        private String title;
        private String name;
        private String sub;
        private String timeLeft;

        // PublicChallenge 엔티티를 받아 필드를 초기화합니다.
        public JoinableDTO(PublicChallenge challenge) {
            this.id = challenge.getId();
            this.title = formatTitle(challenge.getTargetDistance());
            this.name = challenge.getName();
            this.sub = challenge.getSub();
            this.timeLeft = formatTimeLeft(challenge.getEndDate());
        }
    }

    // 이전 챌린지 DTO
    @Data
    public static class PastChallengeDTO {
        private Integer id;
        private String title;
        private String name;
        private String progress;
        private String status;

        // Challenge 엔티티와 달성 거리를 받아 필드를 초기화합니다.
        public PastChallengeDTO(Challenge challenge, Double achievedDistance) {
            this.id = challenge.getId();
            this.title = formatTitle(challenge.getTargetDistance());
            this.name = challenge.getName();
            this.progress = String.format("%.2f / %.2fkm", achievedDistance / 1000.0, challenge.getTargetDistance() / 1000.0);
            this.status = challenge.getEndDate().format(DateTimeFormatter.ofPattern("yyyy. M. d.에 종료됨"));
        }
    }

    // --- DTO 내부에서 사용하는 정적 헬퍼 메소드들 ---

    private static String formatTitle(Integer targetDistance) {
        return targetDistance / 1000 + "K";
    }

    private static String formatTimeLeft(LocalDateTime endDate) {
        Duration duration = Duration.between(LocalDateTime.now(), endDate);
        if (duration.isNegative() || duration.isZero()) {
            return "종료됨";
        }
        long days = duration.toDays();
        if (days > 0) return days + "일 남음";

        long hours = duration.toHours();
        if (hours > 0) return hours + "시간 남음";

        long minutes = duration.toMinutes();
        return minutes + "분 남음";
    }
}

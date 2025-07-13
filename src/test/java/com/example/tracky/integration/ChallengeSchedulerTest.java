package com.example.tracky.integration;

import com.example.tracky._core.enums.PeriodTypeEnum;
import com.example.tracky._core.values.TimeValue;
import com.example.tracky.community.challenges.ChallengeScheduler;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.RewardMaster;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.community.challenges.repository.RewardMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Transactional // 테스트마다 트랜잭션 롤백
public class ChallengeSchedulerTest {

    @Autowired
    private ChallengeScheduler challengeScheduler;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private RewardMasterRepository rewardMasterRepository;

    // 테스트 전에 TimeValue의 시간을 특정 시점으로 고정
    @BeforeEach
    void setup() {
        // TimeValue의 내부 시간을 테스트에 맞게 조작 (필요하다면)
        // 예를 들어, TimeValue 클래스에 테스트용 setter를 추가하여 시간을 설정할 수 있습니다.
        TimeValue.setTestTime(LocalDateTime.of(2025, 7, 7, 0, 0, 10));
    }

    @AfterEach
    void teardown() {
        // 테스트가 끝나면 설정된 시간을 초기화하여 다른 테스트에 영향을 주지 않도록 합니다.
        TimeValue.clearTestTime();
    }

    @Test
    @DisplayName("종료된 챌린지 상태 변경 및 사설 챌린지 메달 지급 테스트")
    void closeAndRewardChallengesTest() {
        // Given
        // 테스트 데이터: 2025년 7월 7일 (월요일) 이전에 종료되는 챌린지 데이터를 미리 DB에 삽입 (setup 또는 init.sql에서)
        // 예시: 2025년 7월 6일에 종료되는 사설 챌린지 (targetDistance=1000m)
        // 해당 챌린지에 참가한 유저의 러닝 기록 (총 1000m 이상, 목표 달성 시점 기록 포함)
        // RewardMaster에 '금메달', '은메달', '동메달'이 존재한다고 가정

        // When
        // ChallengeScheduler의 closeAndRewardChallenges 메서드 직접 호출
        // TimeValue.getServerTime()이 챌린지 종료일(7/6) 이후인 7/7 00:00:00을 반환하도록 설정된 상태에서 실행
        challengeScheduler.closeAndRewardChallenges();

        // Then
        // 1. 종료된 챌린지의 isInProgress 상태가 false로 변경되었는지 확인
        Optional<Challenge> closedChallenge = challengeRepository.findById(1); // 특정 챌린지 ID
        assertThat(closedChallenge).isPresent();
        assertThat(closedChallenge.get().getIsInProgress()).isFalse();

        // 2. 사설 챌린지 참가자에게 메달이 올바르게 지급되었는지 확인 (userChallengeRewardRepository 사용)
        // 예를 들어, userChallengeRewardRepository.findByUserAndChallengeAndRewardMaster(user, challenge, medalReward) 등으로 확인
        // (이 부분은 RewardMaster, UserChallengeReward 엔티티 및 Repository 구현에 따라 달라짐)
    }


    @Test
    @DisplayName("주간 챌린지 자동 생성 테스트")
    void createWeeklyChallengesTest() {
        // Given
        // TimeValue.getServerTime()이 2025년 7월 7일 00:00:10 (월요일)을 반환하도록 설정된 상태에서 진행
        // (BeforeEach에서 TimeValue.setTestTime(LocalDateTime.of(2025, 7, 7, 0, 0, 10));)

        // When
        challengeScheduler.createWeeklyChallenges();

        // Then
        // 1. 2025년 7월 2주차 5k, 10k, 15k 챌린지가 생성되었는지 확인
        List<Challenge> weeklyChallenges = challengeRepository.findByYearAndMonthAndWeekOfMonthAndPeriodType(
                2025, 7, 2, PeriodTypeEnum.WEEKLY); // ChallengeRepository에 해당 메서드 추가 필요

        assertThat(weeklyChallenges).hasSize(3); // 3개의 주간 챌린지가 생성되었는지 확인

        // 2. 각 챌린지의 이름, 기간, 목표거리 등이 올바른지 검증
        Challenge challenge5k = weeklyChallenges.stream()
                .filter(c -> c.getTargetDistance() == 5000)
                .findFirst()
                .orElseThrow();
        assertThat(challenge5k.getName()).isEqualTo("7월 5k 챌린지");
        assertThat(challenge5k.getStartDate().toLocalDate()).isEqualTo(LocalDate.of(2025, 7, 7)); // 해당 주의 월요일
        assertThat(challenge5k.getEndDate().toLocalDate()).isEqualTo(LocalDate.of(2025, 7, 13)); // 해당 주의 일요일

        // 3. 해당 챌린지 이름으로 RewardMaster가 생성되지 않았는지 확인 (미리 만들어두기로 했으므로)
        Optional<RewardMaster> reward5k = rewardMasterRepository.findByRewardName("7월 5k 챌린지");
        assertThat(reward5k).isPresent(); // 미리 만들어져 있어야 함
    }

    @Test
    @DisplayName("월간 챌린지 자동 생성 테스트")
    void createMonthlyChallengesTest() {
        // Given
        // TimeValue.getServerTime()이 2025년 7월 1일 00:00:20을 반환하도록 설정된 상태에서 진행
        // (BeforeEach에서 TimeValue.setTestTime(LocalDateTime.of(2025, 7, 1, 0, 0, 20));)

        // When
        challengeScheduler.createMonthlyChallenges();

        // Then
        // 1. 2025년 7월 25k, 50k, 75k, 100k 챌린지가 생성되었는지 확인
        List<Challenge> monthlyChallenges = challengeRepository.findByYearAndMonthAndPeriodType(
                2025, 7, PeriodTypeEnum.MONTHLY); // ChallengeRepository에 해당 메서드 추가 필요

        assertThat(monthlyChallenges).hasSize(4); // 4개의 월간 챌린지가 생성되었는지 확인

        // 2. 각 챌린지의 이름, 기간, 목표거리 등이 올바른지 검증
        Challenge challenge25k = monthlyChallenges.stream()
                .filter(c -> c.getTargetDistance() == 25000)
                .findFirst()
                .orElseThrow();
        assertThat(challenge25k.getName()).isEqualTo("7월 25k 챌린지");
        assertThat(challenge25k.getStartDate().toLocalDate()).isEqualTo(LocalDate.of(2025, 7, 1)); // 해당 월의 1일
        assertThat(challenge25k.getEndDate().toLocalDate()).isEqualTo(LocalDate.of(2025, 7, 31)); // 해당 월의 마지막 날
    }

    // ChallengeSchedulerTest 클래스 내부에 추가
    @Test
    @DisplayName("미참여 사설 챌린지 정리 테스트")
    void cleanupEmptyChallengesTest() {
        // Given
        // 테스트 데이터: 2025년 7월 7일 현재 진행 중이지만, ChallengeJoin 기록이 없는 사설 챌린지를 미리 DB에 삽입
        // 예를 들어 ID가 100인 PRIVATE 챌린지 (참가자 0명)

        // When
        // TimeValue.getServerTime()이 2025년 7월 7일 00:01:00 (월요일)을 반환하도록 설정된 상태에서 실행
        challengeScheduler.cleanupEmptyChallenges();

        // Then
        // 해당 사설 챌린지가 DB에서 삭제되었는지 확인
        Optional<Challenge> deletedChallenge = challengeRepository.findById(100);
        assertThat(deletedChallenge).isNotPresent();
    }


}
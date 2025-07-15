package com.example.tracky.community.challenges;

import com.example.tracky._core.constants.ChallengeDescs;
import com.example.tracky._core.enums.ChallengeTypeEnum;
import com.example.tracky._core.enums.PeriodTypeEnum;
import com.example.tracky._core.values.TimeValue;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeCreationService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository; // '관리자' 유저를 찾기 위함

    @Transactional
    public void createWeeklyChallenges() {
        // 기준 날짜를 TimeValue 유틸리티를 통해 가져옴
        LocalDateTime serverTime = TimeValue.getServerTime();
        LocalDate today = serverTime.toLocalDate(); // LocalDateTime에서 날짜 정보만 추출
        int year = today.getYear();
        int month = today.getMonthValue();
        // TemporalField를 사용하여 '이 달의 몇 번째 주'인지 계산
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        int weekOfMonth = today.get(weekFields.weekOfMonth());

        // 주간 챌린지 목표 거리 목록 - 어디에 따로 빼야하나?
        List<Integer> weeklyDistances = List.of(5000, 10000, 15000); // 5k, 10k, 15k

        for (Integer distance : weeklyDistances) {
            // 1. 중복 생성 방지 확인
            boolean exists = challengeRepository.existsByYearAndMonthAndWeekAndPeriod(
                    year, month, weekOfMonth, distance, PeriodTypeEnum.WEEKLY);

            if (exists) continue; // 중복이 존재하면 다음 거리로 생성

            // 2. 챌린지 및 보상 이름 생성
            String challengeName = String.format("%d월 %dk 챌린지", month, distance / 1000);

            // 3. 챌린지 기간 계산
            LocalDateTime startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
            LocalDateTime endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).atTime(LocalTime.MAX);

            // 4. Challenge 엔티티 생성
            Challenge newChallenge = Challenge.builder()
                    .name(challengeName)
                    .sub(String.format("이번 주 %dk를 달려보세요!", distance / 1000))
                    .description(ChallengeDescs.WEEKLY_CHALLENGE_DESC)
                    .targetDistance(distance)
                    .startDate(startOfWeek)
                    .endDate(endOfWeek)
                    .type(ChallengeTypeEnum.PUBLIC)
                    .periodType(PeriodTypeEnum.WEEKLY)
                    .challengeYear(year)
                    .challengeMonth(month)
                    .weekOfMonth(weekOfMonth)
                    .isInProgress(true)
                    .imgIndex(null) // TODO : 이미지 추가
                    .build();

            // 5. 챌린지 저장
            challengeRepository.save(newChallenge);

            log.info("주간 챌린지가 생성되었습니다.");

        }
    }

    @Transactional
    public void createMonthlyChallenges() {
        // 기준 날짜를 TimeValue 유틸리티를 통해 가져옴
        LocalDateTime serverTime = TimeValue.getServerTime();
        LocalDate today = serverTime.toLocalDate();
        int year = today.getYear();
        int month = today.getMonthValue();

        // 월간 챌린지 목표 거리 목록 - 거리목록 따로 빼야하나? 어디로?
        List<Integer> monthlyDistances = List.of(25000, 50000, 75000, 100000);

        for (Integer distance : monthlyDistances) {
            // 1. 중복 생성 방지 확인 (weekOfMonth 조건은 제외)
            boolean exists = challengeRepository.existsByYearAndMonthAndPeriod(
                    year, month, distance, PeriodTypeEnum.MONTHLY);

            if (exists) continue;

            String challengeName = String.format("%d월 %dk 챌린지", month, distance / 1000);

            // 2. 챌린지 기간 계산 (해당 월의 시작일과 마지막일)
            LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(LocalTime.MAX);

            // 3. Challenge 엔티티 생성
            Challenge newChallenge = Challenge.builder()
                    .name(challengeName)
                    .sub(String.format("이번 달 %dk를 달려보세요!", distance / 1000))
                    .description(ChallengeDescs.MONTHLY_CHALLENGE_DESC)
                    .targetDistance(distance)
                    .startDate(startOfMonth)
                    .endDate(endOfMonth)
                    .type(ChallengeTypeEnum.PUBLIC)
                    .periodType(PeriodTypeEnum.MONTHLY)
                    .challengeYear(year)
                    .challengeMonth(month)
                    .isInProgress(true)
                    .imgIndex(null) // TODO : 이미지 추가
                    .build();

            // 4. 챌린지 저장
            challengeRepository.save(newChallenge);

            log.info("월간 챌린지가 생성되었습니다.");

        }
    }
}

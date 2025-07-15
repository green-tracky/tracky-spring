package com.example.tracky.community.leaderboard;

import com.example.tracky._core.enums.DateRangeType;
import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeJoin;
import com.example.tracky.community.challenges.repository.ChallengeJoinRepository;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.friends.Friend;
import com.example.tracky.user.friends.FriendRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderBoardService {
    private final RunRecordRepository runRecordRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final ChallengeJoinRepository challengeJoinRepository;

    public LeaderBoardsResponse.LeaderBoardDTO getLeaderBoards(OAuthProfile sessionProfile, LocalDate baseDate, Integer before, DateRangeType dateRangeType) {
        LocalDate start;
        LocalDate end;

        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        switch (dateRangeType) {
            case MONTH -> {
                LocalDate targetDate = baseDate.minusMonths(before);
                start = targetDate.with(TemporalAdjusters.firstDayOfMonth());
                end = targetDate.with(TemporalAdjusters.lastDayOfMonth());
            }
            case YEAR -> {
                LocalDate targetDate = baseDate.minusYears(before);
                start = targetDate.with(TemporalAdjusters.firstDayOfYear());
                end = targetDate.with(TemporalAdjusters.lastDayOfYear());
            }
            // datetype == null 이면 WEEK(기본값)
            default -> {
                LocalDate targetDate = baseDate.minusWeeks(before);
                start = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                end = start.plusDays(6);
            }
        }


        // 1. 기준 주 계산
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 2. 친구 목록 조회 및 친구 유저 리스트 추출
        List<Friend> friendList = friendRepository.findfriendByUserIdJoinFriend(userPS.getId());
        List<User> friendUsers = new ArrayList<>();
        for (Friend friend : friendList) {
            // 내가 fromUser면 상대는 toUser
            if (friend.getFromUser().getId().equals(userPS.getId())) {
                friendUsers.add(friend.getToUser());
            }
            // 내가 toUser면 상대는 fromUser
            else if (friend.getToUser().getId().equals(userPS.getId())) {
                friendUsers.add(friend.getFromUser());
            }
        }

        // 4. 내 정보 조회
        User me = userRepository.findByIdJoin(userPS.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 3. 전체 유저 리스트 (나 + 친구들)
        List<User> allUsers = new ArrayList<>();
        allUsers.add(me);
        for (User friendUser : friendUsers) {
            allUsers.add(friendUser);
        }

        // 4. 전체 유저 ID 리스트
        List<Integer> allUserIds = new ArrayList<>();
        for (User myUser : allUsers) {
            allUserIds.add(myUser.getId());
        }

        // 5. 모든 사람 러닝 기록 조회
        List<RunRecord> runRecords = runRecordRepository.findAllByCreatedAtBetween(allUserIds, startTime, endTime);

        // 6. 유저별 거리 누적용 Map 초기화
        Map<Integer, Integer> userDistanceMap = new HashMap<>();
        for (User u : allUsers) {
            userDistanceMap.put(u.getId(), 0);
        }

        // 7. 각 기록마다 해당 유저 ID 기준 거리 누적
        for (RunRecord runRecord : runRecords) {
            Integer userId = runRecord.getUser().getId();
            Integer currentDistance = userDistanceMap.getOrDefault(userId, 0);
            userDistanceMap.put(userId, currentDistance + runRecord.getTotalDistanceMeters());
        }

        // 8. 유저별 DTO 생성 + 거리 기준 정렬
        List<LeaderBoardsResponse.RankingListDTO> rankingList = new ArrayList<>();
        for (User u : allUsers) {
            String username = u.getUsername();
            String profileUrl = u.getProfileUrl();
            int userDistance = userDistanceMap.getOrDefault(u.getId(), 0);
            rankingList.add(new LeaderBoardsResponse.RankingListDTO(username, profileUrl, userDistance, 0, u.getId()));
        }
        rankingList.sort((a, b) -> b.getTotalDistanceMeters().compareTo(a.getTotalDistanceMeters()));

        // 9. 랭킹 계산
        List<LeaderBoardsResponse.RankingListDTO> newRankingList = new ArrayList<>();
        int rank = 1;
        int prevDistance = -1;
        int actualRank = 1; // 표시될 순위

        for (int i = 0; i < rankingList.size(); i++) {
            LeaderBoardsResponse.RankingListDTO dto = rankingList.get(i);
            int distance = dto.getTotalDistanceMeters();

            if (distance != prevDistance) {
                actualRank = rank; // 현재 인덱스를 기반으로 순위 갱신
                prevDistance = distance;
            }

            newRankingList.add(new LeaderBoardsResponse.RankingListDTO(
                    dto.getUsername(),
                    dto.getProfileUrl(),
                    distance,
                    actualRank,
                    dto.getUserId()
            ));

            rank++;
        }

        rankingList = newRankingList;

        // 10. 나의 랭킹 구하기
        int myDistance = userDistanceMap.getOrDefault(userPS.getId(), 0);
        int myRank = 0;
        for (LeaderBoardsResponse.RankingListDTO dto : rankingList) {
            if (dto.getUserId() == userPS.getId()) {
                myRank = dto.getRank();
                break;
            }
        }

        LeaderBoardsResponse.MyRankingDTO myRanking = new LeaderBoardsResponse.MyRankingDTO(myDistance, myRank);

        log.info("{}({})이 친구 리더보드를 조회합니다.", userPS.getUsername(), userPS.getId());

        return new LeaderBoardsResponse.LeaderBoardDTO(myRanking, rankingList);
    }

    public LeaderBoardsResponse.ChallengeLeaderBoardDTO getChallengeLeaderBoards(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 챌린지에 참가했으면 처리 가능
        ChallengeJoin challengeJoinPS = challengeJoinRepository.findByChallengeIdAndUserId(id, userPS.getId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.CHALLENGE_JOIN_NOT_FOUND));


        // 1. 챌린지 조회
        Challenge challengeDate = challengeJoinPS.getChallenge();

        // 2. 해당 챌린지 날짜 조회
        LocalDateTime start = challengeDate.getStartDate();
        LocalDateTime end = challengeDate.getEndDate();

        // 3. 해당 챌린지 참여자 조회
        List<User> userList = challengeJoinRepository.findUserAllById(id);

        // 4. 유저별 거리 기록 조회 및 DTO 생성
        List<LeaderBoardsResponse.RankingListDTO> rankingList = new ArrayList<>();
        for (User u : userList) {
            int distance = runRecordRepository.findTotalDistanceByUserIdAndDateRange(u.getId(), start, end);
            rankingList.add(new LeaderBoardsResponse.RankingListDTO(
                    u.getUsername(),
                    u.getProfileUrl(),
                    distance,
                    0, // 초기 랭킹 (나중에 정렬 후 재계산)
                    u.getId()
            ));
        }

        rankingList.sort((a, b) -> b.getTotalDistanceMeters().compareTo(a.getTotalDistanceMeters()));

        // 7. 해당 챌린지 유저 랭크
        List<LeaderBoardsResponse.RankingListDTO> newRankingList = new ArrayList<>();
        int rank = 1;
        int prevDistance = -1;
        int actualRank = 1; // 표시될 순위

        for (int i = 0; i < rankingList.size(); i++) {
            LeaderBoardsResponse.RankingListDTO dto = rankingList.get(i);
            int distance = dto.getTotalDistanceMeters();

            if (distance != prevDistance) {
                actualRank = rank; // 현재 인덱스를 기반으로 순위 갱신
                prevDistance = distance;
            }

            newRankingList.add(new LeaderBoardsResponse.RankingListDTO(
                    dto.getUsername(),
                    dto.getProfileUrl(),
                    distance,
                    actualRank,
                    dto.getUserId()
            ));
            rank++;
        }
        rankingList = newRankingList;

        log.info("{}({})이 {}({}) 챌린지의 리더보드를 조회합니다.", userPS.getUsername(), userPS.getId(), challengeJoinPS.getChallenge().getName(), challengeJoinPS.getChallenge().getId());

        return new LeaderBoardsResponse.ChallengeLeaderBoardDTO(rankingList);
    }
}
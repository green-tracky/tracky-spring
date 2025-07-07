package com.example.tracky.community.leaderboard;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.friends.Friend;
import com.example.tracky.user.friends.FriendRepository;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class LeaderBoardService {
    private final RunRecordRepository runRecordRepository;
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;


    public LeaderBoardsResponse.MainDTO getLederBoards(User user, LocalDate baseDate, Integer before) {
        // 1. 기준 주 계산
        LocalDate targetDate = baseDate.minusWeeks(before);
        LocalDate start = targetDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = start.plusDays(6);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(LocalTime.MAX);

        // 2. 친구 목록 조회 및 친구 유저 리스트 추출
        List<Friend> friendList = friendRepository.findfriendByUserIdJoinFriend(user.getId());
        List<User> friendUsers = new ArrayList<>();
        for (Friend friend : friendList) {
            friendUsers.add(friend.getToUser());
        }

        // 4. 내 정보 조회
        User me = userRepository.findByIdJoin(user.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

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
            System.out.println("유저 아이디들 : " + myUser.getId());
        }

        // 5. 모든 사람 러닝 기록 조회
        List<RunRecord> runRecords = runRecordRepository.findAllByCreatedAtBetween(allUserIds, startTime, endTime);
        Integer totalDistanceMeters = 0;
        for (RunRecord runRecord : runRecords) {
            totalDistanceMeters += runRecord.getTotalDistanceMeters();
        }

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

        // 8. 유저별 DTO 생성
        List<LeaderBoardsResponse.RankingListDTO> rankingList = new ArrayList<>();
        for (User u : allUsers) {
            int userDistance = userDistanceMap.getOrDefault(u.getId(), 0);
            rankingList.add(new LeaderBoardsResponse.RankingListDTO(u, userDistance));
        }

        int myDistance = userDistanceMap.getOrDefault(user.getId(), 0);
        LeaderBoardsResponse.MyRankingDTO myRanking = new LeaderBoardsResponse.MyRankingDTO(myDistance);

        return new LeaderBoardsResponse.MainDTO(myRanking, rankingList);
    }
}
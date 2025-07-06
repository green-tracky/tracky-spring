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
import java.util.List;

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

        // 3. 전체 유저 리스트 (나 + 친구들)
        List<User> allUsers = new ArrayList<>();
        allUsers.add(user);
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
        Integer totalDistanceMeters = 0;
        for (RunRecord runRecord : runRecords) {
            totalDistanceMeters += runRecord.getTotalDistanceMeters();
        }

        // 6. 리스트 DTO에 하나씩 변환
        List<LeaderBoardsResponse.RankingListDTO> rankingList = new ArrayList<>();
        for (User u : allUsers) {
            Integer distance = distanceMap.getOrDefault(u.getId(), 0);
            rankingList.add(new LeaderBoardsResponse.RankingListDTO(u, distance));
        }


        LeaderBoardsResponse.MyRankingDTO myRanking = new LeaderBoardsResponse.MyRankingDTO(totalDistanceMeters);
        List<LeaderBoardsResponse.RankingListDTO> rankingList = new LeaderBoardsResponse.RankingListDTO(allUsers, totalDistanceMeters);


        return new LeaderBoardsResponse.MainDTO(myRanking, rankingList);
    }
}
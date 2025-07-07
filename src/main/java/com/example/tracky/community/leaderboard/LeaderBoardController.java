package com.example.tracky.community.leaderboard;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.community.leaderboard.enums.DateEnums;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class LeaderBoardController {

    private final LeaderBoardService leaderBoardService;

    @GetMapping("/community/leaderboards/week")
    public ResponseEntity<?> getLeaderBoardsWeek(@RequestParam(value = "before", defaultValue = "0") Integer before) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        // before가 0~1 사이가 아니면 0으로 기본 처리 (범위 제한)
        if (before == null || before < 0 || before > 1) {
            before = 0;
        }

        // TODO : 전역변수로 나중에 설정
//        LocalDate baseDate = LocalDate.now();
        LocalDate baseDate = LocalDate.of(2025, 06, 05);

        LeaderBoardsResponse.LeaderBoardDTO respDTO = leaderBoardService.getLeaderBoards(user, baseDate, before, DateEnums.WEEKDATE);
        return Resp.ok(respDTO);
    }

    @GetMapping("/community/leaderboards/mouth")
    public ResponseEntity<?> getLeaderBoardsMouth(@RequestParam(value = "before", defaultValue = "0") Integer before) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        // before가 0~1 사이가 아니면 0으로 기본 처리 (범위 제한)
        if (before == null || before < 0 || before > 1) {
            before = 0;
        }

        // TODO : 전역변수로 나중에 설정
        LocalDate baseDate = LocalDate.now();

        LeaderBoardsResponse.LeaderBoardDTO respDTO = leaderBoardService.getLeaderBoards(user, baseDate, before, DateEnums.MONTHDATE);
        return Resp.ok(respDTO);
    }

    @GetMapping("/community/leaderboards/year")
    public ResponseEntity<?> getLeaderBoardsYear() {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        // before가 0 밖에 없음
        Integer before = 0;

        // TODO : 전역변수로 나중에 설정
        LocalDate baseDate = LocalDate.now();

        LeaderBoardsResponse.LeaderBoardDTO respDTO = leaderBoardService.getLeaderBoards(user, baseDate, before, DateEnums.YEARDATE);
        return Resp.ok(respDTO);
    }

}

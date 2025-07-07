package com.example.tracky.community.leaderboard;

import com.example.tracky._core.utils.Resp;
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

    @GetMapping("/community/leaderboards")
    // param 추가 - 필터용
    public ResponseEntity<?> getLeaderBoards(@RequestParam(value = "before", defaultValue = "0") Integer before) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        // before가 0~4 사이가 아니면 0으로 기본 처리 (범위 제한)
        if (before == null || before < 0 || before > 4) {
            before = 0;
        }

        LocalDate baseDate = LocalDate.now();

        LeaderBoardsResponse.MainDTO respDTO = leaderBoardService.getLederBoards(user, baseDate, before);
        return Resp.ok(respDTO);
    }

    @GetMapping("/community/leaderboards/test")
    // param 추가 - 필터용
    public ResponseEntity<?> getLeaderBoardsTest(@RequestParam(value = "before", defaultValue = "0") Integer before) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        // before가 0~4 사이가 아니면 0으로 기본 처리 (범위 제한)
        if (before == null || before < 0 || before > 4) {
            before = 0;
        }

        LocalDate baseDate = LocalDate.of(2025, 06, 3);

        LeaderBoardsResponse.MainDTO respDTO = leaderBoardService.getLederBoards(user, baseDate, before);
        return Resp.ok(respDTO);
    }
}

package com.example.tracky.user.runlevel;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunLevelController {

    private final RunLevelService runLevelService;

    @GetMapping("/run-levels")
    public ResponseEntity<?> getRunLevels() {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        RunLevelResponse.ListDTO respDTO = runLevelService.getRunLevels(user);
        return Resp.ok(respDTO);
    }

}

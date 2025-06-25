package com.example.tracky.runrecord;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunRecordController {

    private final RunRecordService runRecordsService;

    @PostMapping("/runs")
    public ResponseEntity<?> save(@RequestBody RunRecordRequest.SaveDTO reqDTO) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        // 필터에서 가져올거 미리 가져옴 나중에 세션에서 가져와야함
        User user = User.builder().id(userId).build();

        RunRecordResponse.SaveDTO respDTO = runRecordsService.save(user, reqDTO);

        return Resp.ok(respDTO);
    }

    @GetMapping("/runs/{id}")
    public ResponseEntity<?> getRunRecord(@PathVariable Integer id) {
        log.debug("러닝 아이디" + id);
        RunRecordResponse.DetailDTO respDTO = runRecordsService.getRunRecord(id);
        return Resp.ok(respDTO);
    }

}
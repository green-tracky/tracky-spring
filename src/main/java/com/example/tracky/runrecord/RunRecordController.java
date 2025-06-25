package com.example.tracky.runrecord;

import com.example.tracky._core.utils.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunRecordController {

    private final RunRecordService runRecordsService;

    @PostMapping("/runs")
    public ResponseEntity<?> save(@RequestBody RunRecordRequest.SaveDTO reqDTO) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        RunRecordResponse.SaveDTO respDTO = runRecordsService.save(userId, reqDTO);

        return Resp.ok(respDTO);
    }

    @GetMapping("/runs/{id}")
    public ResponseEntity<?> getRunRecord(@PathVariable Integer id) {
        RunRecordResponse.DetailDTO respDTO = runRecordsService.getRunRecord(id);
        return Resp.ok(respDTO);
    }

}
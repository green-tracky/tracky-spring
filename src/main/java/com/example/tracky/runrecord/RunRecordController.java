package com.example.tracky.runrecord;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracky._core.utils.Resp;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunRecordController {

    private final RunRecordService runRecordsService;

    @PostMapping("/runs")
    public ResponseEntity<?> save(@RequestBody RunRecordRequest.DTO reqDTO) {
        // 유저 아이디를 임시로 1 로 함
        Integer userId = 1;

        RunRecordResponse.DTO respDTO = runRecordsService.save(userId, reqDTO);

        return Resp.ok(respDTO);
    }

}
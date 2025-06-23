package com.example.tracky.runrecord;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracky._core.utils.Resp;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RunRecordController {

    private final RunRecordService runRecordsService;

    @GetMapping("/runs")
    public ResponseEntity<?> getRunRecords() {
        RunRecordResponse.MainPageDTO respDTO = runRecordsService.getRunRecords();
        System.err.println("Controller : "+respDTO);
        return Resp.ok(respDTO);
    }

}
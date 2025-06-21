package com.example.tracky.runrecord;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RunRecordController {

    private final RunRecordService runRecordsService;

    @GetMapping("/s/api/runs/{id}")
    public void getRunRecord() {

    }
}
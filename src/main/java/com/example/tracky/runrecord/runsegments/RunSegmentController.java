package com.example.tracky.runrecord.runsegments;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RunSegmentController {

    private final RunSegmentService runRecordsService;

}
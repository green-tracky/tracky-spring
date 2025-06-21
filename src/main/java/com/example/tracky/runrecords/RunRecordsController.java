package com.example.tracky.runrecords;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RunRecordsController {

    private final RunRecordsService runRecordsService;

}
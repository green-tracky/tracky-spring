package com.example.tracky.runrecord.runbadge;

import org.springframework.web.bind.annotation.RestController;

import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RunBadgeController {

    private final RunBadgeAchvService runRecordsService;

}
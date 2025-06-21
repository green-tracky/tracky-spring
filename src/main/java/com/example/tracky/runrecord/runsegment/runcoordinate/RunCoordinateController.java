package com.example.tracky.runrecord.runsegment.runcoordinate;

import org.springframework.web.bind.annotation.RestController;

import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RunCoordinateController {

    private final RunBadgeAchvService runRecordsService;

}
package com.example.tracky.runrecord.runsegments.runcoordinates;

import org.springframework.web.bind.annotation.RestController;

import com.example.tracky.runrecord.runbadges.runbadgeachv.RunBadgeAchvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RunCoordinateController {

    private final RunBadgeAchvService runRecordsService;

}
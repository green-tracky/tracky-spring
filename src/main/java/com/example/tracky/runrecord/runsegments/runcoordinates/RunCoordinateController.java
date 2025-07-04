package com.example.tracky.runrecord.runsegments.runcoordinates;

import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchvService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RunCoordinateController {

    private final RunBadgeAchvService runRecordsService;

}
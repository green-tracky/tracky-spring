package com.example.tracky.runrecord.pictures;

import com.example.tracky.runrecord.runbadges.runbadgeachvs.RunBadgeAchvService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PictureController {

    private final RunBadgeAchvService runRecordsService;

}
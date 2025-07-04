package com.example.tracky.runrecord.pictures;

import org.springframework.web.bind.annotation.RestController;

import com.example.tracky.runrecord.runbadges.runbadgeachv.RunBadgeAchvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PictureController {

    private final RunBadgeAchvService runRecordsService;

}
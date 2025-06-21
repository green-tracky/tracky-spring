package com.example.tracky.runrecord.picture;

import org.springframework.web.bind.annotation.RestController;

import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PictureController {

    private final RunBadgeAchvService runRecordsService;

}
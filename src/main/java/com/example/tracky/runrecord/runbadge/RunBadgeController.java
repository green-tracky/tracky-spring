package com.example.tracky.runrecord.runbadge;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RunBadgeController {

    private final RunBadgeService runBadgeService;

    @GetMapping("/run-badges")
    public List<RunBadgeResponse.RunBadgeResponseDto> 뱃지조회() {
        return runBadgeService.뱃지조회();
    }

}
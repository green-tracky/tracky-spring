package com.example.tracky.runrecord.runbadge;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunBadgeController {

    private final RunBadgeService runBadgeService;

    @GetMapping("/run-badges")
    public ResponseEntity<?> getBadges() {
        List<RunBadgeResponse.DTO> respDTO = runBadgeService.getAllBadges();
        return Resp.ok(respDTO);
    }

}
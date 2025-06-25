package com.example.tracky.runrecord.runbadge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunBadgeService {

    private final RunBadgeRepository runBadgeRepository;

    public List<RunBadgeResponse.DTO> getRunBadges() {

        // 1. 뱃지들 조회
        List<RunBadge> runBadges = runBadgeRepository.findAll();

        // 2. 응답 DTO로 변환
        return runBadges.stream()
                .map(badge -> new RunBadgeResponse.DTO(badge))
                .toList();
    }

}
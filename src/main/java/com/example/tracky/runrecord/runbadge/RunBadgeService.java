package com.example.tracky.runrecord.runbadge;

import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchvRepository;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunBadgeService {

    private final RunBadgeRepository runBadgeRepository;
    private final RunBadgeAchvRepository runBadgeAchvRepository;

    public RunBadgeResponse.ListDTO getRunBadges(User user) {
        // 1. 조회
        List<RunBadge> runBadgesPS = runBadgeRepository.findAll();
        List<RunBadgeAchv> runBadgeAchvsPS = runBadgeAchvRepository.findByUserIdJoin(user.getId());

        // 2. 응답 DTO 로 변환
        return new RunBadgeResponse.ListDTO(runBadgesPS, runBadgeAchvsPS);
    }

}
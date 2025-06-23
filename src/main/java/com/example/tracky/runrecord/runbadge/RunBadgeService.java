package com.example.tracky.runrecord.runbadge;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.tracky.runrecord.runbadge.RunBadgeResponse.RunBadgeResponseDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunBadgeService {

    private final RunBadgeRepository runBadgeRepository;

    @Transactional
    public List<RunBadgeResponse.RunBadgeResponseDto> 뱃지조회() {
        List<RunBadge> runBadges = runBadgeRepository.findAll();

        return runBadges.stream()
                .map(badge -> new RunBadgeResponse.RunBadgeResponseDto(
                        badge.getId(),
                        badge.getName(),
                        badge.getDescription(),
                        badge.getImageUrl()
                ))
                .collect(Collectors.toList());
    }

}
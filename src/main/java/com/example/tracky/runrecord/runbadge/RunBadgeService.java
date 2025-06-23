package com.example.tracky.runrecord.runbadge;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.tracky.runrecord.runbadge.RunBadgeResponse.DTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RunBadgeService {

    private final RunBadgeRepository runBadgeRepository;

    @Transactional
    public List<RunBadgeResponse.DTO> getAllBadges() {
        List<RunBadge> runBadges = runBadgeRepository.findAll();

        return runBadges.stream()
                .map(badge -> new RunBadgeResponse.DTO(badge))
                .toList();
    }

}
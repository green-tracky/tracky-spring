package com.example.tracky.user.runlevel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RunLevelService {

    private final RunLevelRepository runLevelRepository;

    public List<RunLevelResponse.DTO> getRunLevels() {
        List<RunLevel> runLevels = runLevelRepository.findAll();

        return runLevels.stream()
                .map(runLevel -> new RunLevelResponse.DTO(runLevel))
                .toList();
    }

}

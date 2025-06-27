package com.example.tracky.user.runlevel;

import com.example.tracky._core.utils.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class RunLevelController {

    private final RunLevelService runLevelService;

    @GetMapping("/run-level")
    public ResponseEntity<?> getRunLevels() {
        List<RunLevelResponse.DTO> respDTO = runLevelService.getRunLevels();
        return Resp.ok(respDTO);
    }

}

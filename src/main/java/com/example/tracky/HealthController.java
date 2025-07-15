package com.example.tracky;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    /**
     * 로드 벨런서 헬스체크 주소
     *
     * @return
     */
    @GetMapping("/health")
    public String health() {
        return "<h1>tracky ok</h1>";
    }
}

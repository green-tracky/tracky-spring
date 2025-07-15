package com.example.tracky.sentryio;

import io.sentry.Sentry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SentryIOController {
    @GetMapping("/sentry-test")
    public String testManualError() {
        try {
            // 예외를 강제로 발생시킵니다.
            throw new RuntimeException("개발자가 직접 Sentry로 보낸 테스트 에러입니다.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }
        return "수동 에러 전송 완료! Sentry 대시보드를 확인하세요.";
    }
}

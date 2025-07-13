package com.example.tracky._core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.reflect.Method;

@Slf4j
@Configuration
@EnableAsync // Spring에서 비동기 기능을 활성화하는 어노테이션입니다.
public class AsyncConfig implements AsyncConfigurer {

    // 비동기 메소드에서 발생한, 잡히지 않은 예외를 처리할 핸들러를 반환합니다.
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    /**
     * @Async 예외를 처리하기 위한 커스텀 핸들러 클래스입니다.
     */
    public static class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            // [권장] 예외 상황이므로 'error' 레벨로 기록합니다.
            // 또한, ex 객체를 함께 넘겨주면 스택 트레이스(오류 발생 경로) 전체가 상세하게 기록됩니다.
            log.error("### 비동기 작업 실패 ### Method: {}, Message: {}", method.getName(), ex.getMessage(), ex);

            // 파라미터 값도 상세히 기록하여 원인 분석을 돕습니다.
            for (Object param : params) {
                log.error("Parameter: {}", param.toString());
            }
        }
    }
}

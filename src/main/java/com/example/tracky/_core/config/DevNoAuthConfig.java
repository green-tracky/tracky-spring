package com.example.tracky._core.config;

import com.example.tracky._core.filter.MockUserFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 'dev-noauth' 프로필 전용 설정 클래스.
 * 이 클래스의 Bean들은 'dev-noauth' 프로필이 활성화되었을 때만 스프링 컨테이너에 등록됩니다.
 */
@Slf4j
@Configuration
// 주석: 프로필 이름을 'dev-noauth'로 변경했습니다.
// 이제 애플리케이션 실행 시 active profile이 'dev-noauth'일 때만 이 설정이 동작합니다.
@Profile("dev-noauth")
public class DevNoAuthConfig { // 클래스 이름도 변경해주면 더 좋습니다.

    @Bean
    public FilterRegistrationBean<MockUserFilter> mockUserFilterRegistration() {
        log.debug("DEV-NOAUTH 모드: MockUserFilter를 등록합니다.");
        FilterRegistrationBean<MockUserFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MockUserFilter());
        registrationBean.addUrlPatterns("/s/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }
}

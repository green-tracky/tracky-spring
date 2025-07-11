package com.example.tracky._core.config;

import com.example.tracky._core.filter.AuthorizationFilter;
import com.example.tracky._core.filter.LogFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    // bean 에서 관리해라. 스프링이 주입 해줘라
    private final AuthorizationFilter authorizationFilter;

    // 주석: 프로필 이름을 'dev-noauth'로 변경했습니다.
    // 이제 'dev-noauth' 프로필이 아닐 때만 이 Bean이 등록됩니다.
    @Profile("!dev-noauth")
    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistration() {
        FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authorizationFilter);
        registrationBean.addUrlPatterns("/s/*"); // 모든 요청에 적용
        registrationBean.setOrder(2); // 필터 순서 설정
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<LogFilter> loggingFilter() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter());
        registrationBean.addUrlPatterns("/*"); // 모든 요청에 적용
        registrationBean.setOrder(1); // 필터 순서 설정
        return registrationBean;
    }
}

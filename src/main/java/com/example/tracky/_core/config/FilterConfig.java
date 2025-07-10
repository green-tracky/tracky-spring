package com.example.tracky._core.config;

import com.example.tracky._core.filter.AuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    // bean 에서 관리해라. 스프링이 주입 해줘라
    private final AuthorizationFilter authorizationFilter;

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistration() {
        FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authorizationFilter);
        registrationBean.addUrlPatterns("/s/*"); // 모든 요청에 적용
        registrationBean.setOrder(2); // 필터 순서 설정
        return registrationBean;
    }

//    @Bean
//    public FilterRegistrationBean<LogFilter> loggingFilter() {
//        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new LogFilter());
//        registrationBean.addUrlPatterns("/*"); // 모든 요청에 적용
//        registrationBean.setOrder(3); // 필터 순서 설정
//        return registrationBean;
//    }
}

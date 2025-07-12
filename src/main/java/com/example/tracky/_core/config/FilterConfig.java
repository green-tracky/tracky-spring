package com.example.tracky._core.config;

import com.example.tracky._core.filter.AuthorizationFilter;
import com.example.tracky._core.filter.LogFilter;
import com.example.tracky.user.kakaojwt.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    // 주석: AuthorizationFilter를 생성하기 위해 RSAUtil Bean은 여전히 필요합니다.
    private final RSAUtil rsaUtil;

    /**
     * [핵심] 'dev-noauth' 프로필이 아닐 때만 AuthorizationFilter를 스프링 Bean으로 생성합니다.
     * 1. @Bean 어노테이션이 붙어있으므로, 이 메서드가 반환하는 객체는 Spring 컨테이너가 관리하는 Bean이 됩니다.
     * 2. Spring이 이 Bean을 생성할 때 생성자인 new AuthorizationFilter(rsaUtil)를 호출하며,
     * 파라미터인 rsaUtil을 IoC 컨테이너에서 찾아 자동으로 주입(DI)해줍니다.
     * 3. 결과적으로, 통합 테스트 시 RSAUtil을 @MockBean으로 교체하는 것이 가능해집니다.
     */
    @Bean
    @Profile("!dev-noauth")
    public AuthorizationFilter authorizationFilter() {
        // 주석: 이 메서드의 목적은 Spring이 관리하는 AuthorizationFilter Bean을 만드는 것입니다.
        return new AuthorizationFilter(rsaUtil);
    }

    /**
     * 'dev-noauth' 프로필이 아닐 때만, 위에서 생성된 AuthorizationFilter Bean을 필터 체인에 등록합니다.
     *
     * @param filter Spring이 위 authorizationFilter() 메서드를 통해 생성한 Bean을 여기에 자동으로 주입해줍니다.
     */
    @Bean
    @Profile("!dev-noauth")
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistration(AuthorizationFilter filter) {
        FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<>();

        // 주석: new로 직접 생성하는 대신, Spring이 관리하는 Bean(filter)을 주입받아 등록합니다.
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/s/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<LogFilter> loggingFilter() {
        FilterRegistrationBean<LogFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}

package com.example.tracky._core.filter;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 'dev' 프로필에서만 동작하는 필터.
 * 실제 인증 과정 없이 가짜(Mock) 사용자 정보를 세션에 주입하여
 * 마치 로그인된 것처럼 테스트할 수 있게 해준다.
 */
@Slf4j
public class MockUserFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. 서블릿 요청/응답을 HTTP용으로 캐스팅합니다.
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 2. 세션을 가져옵니다. (세션이 없으면 새로 생성)
        HttpSession session = httpRequest.getSession(true);

        // 3. 세션에 이미 사용자 정보가 있는지 확인합니다.
        //    (없을 때만 가짜 정보를 넣어주어, 불필요한 객체 생성을 방지합니다)
        if (session.getAttribute(SessionKeys.PROFILE) == null) {
            log.debug("세션에 사용자 정보가 없어 Mock 유저를 생성합니다.");

            // 4. 테스트에 사용할 가짜 OAuthProfile 객체를 생성합니다.
            //    컨트롤러나 서비스에서 필요한 값을 위주로 채워줍니다.
            OAuthProfile mockProfile = new OAuthProfile();
            mockProfile.setSub("123456789"); // 고유 사용자 ID
            mockProfile.setNickname("테스트유저");      // 닉네임
            mockProfile.setPicture("https://placehold.co/600x400/EEE/31343C?text=Test_User"); // 프로필 사진 URL
            mockProfile.setIss("https://kauth.kakao.com"); // 발급자

            // 5. 생성한 가짜 사용자 정보를 세션에 저장합니다.
            //    키 값은 실제 AuthorizationFilter에서 사용하는 `SessionKeys.PROFILE`과 동일해야 합니다.
            session.setAttribute(SessionKeys.PROFILE, mockProfile);
        }

        // 6. 다음 필터로 요청을 전달합니다.
        chain.doFilter(request, response);
    }
}

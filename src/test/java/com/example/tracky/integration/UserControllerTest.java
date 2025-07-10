package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class UserControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    @DisplayName("카카오 로그인")
    void kakao_login_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/api/oauth/kakao/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));

        // user.id
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.user.id").value(1));

        // user.username
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.user.username").value("ssar"));

        // user.loginId
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.user.loginId").value("KAKAO_123456789"));

        // idToken 패턴: JWT (header.payload.signature) 형태
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.idToken").value(Matchers.matchesPattern("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$")));

        // createdAt 날짜 포맷: yyyy-MM-dd HH:mm:ss
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.user.createdAt").value(Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));

        // userTag: #XXXXXX (Hex 컬러코드 형태)
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.user.userTag").value(Matchers.matchesPattern("^#[A-Fa-f0-9]{6}$")));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
}

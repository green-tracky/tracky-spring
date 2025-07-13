package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class NotificationControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    void get_notifications_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/notifications")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // myChallenges[0]
        actions.andExpect(jsonPath("$.data.notifications[0].type").value("challengeInvite"));
        actions.andExpect(jsonPath("$.data.notifications[0].profileUrl").value("http://example.com/profiles/leo.jpg"));
        actions.andExpect(jsonPath("$.data.notifications[0].username").value("leo"));
        actions.andExpect(jsonPath("$.data.notifications[0].status").value("대기"));
        actions.andExpect(jsonPath("$.data.notifications[0].createdAt").value(matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));


        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }


}

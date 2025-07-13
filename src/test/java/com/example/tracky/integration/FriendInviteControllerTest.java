package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class FriendInviteControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    @DisplayName("친구 요청 성공")
    void friend_invite_test() throws Exception {
        // given
        Integer toUserId = 3;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/friend/invite/{toUserId}", toUserId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.id").value(6));
        actions.andExpect(jsonPath("$.data.fromUser").value(1));
        actions.andExpect(jsonPath("$.data.toUser").value(3));
        actions.andExpect(jsonPath("$.data.status").value("대기"));


        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    // TODO : 친구가 아닙니다로 나오게 수정
    @Test
    void friend_invite_fail_test() throws Exception {
        // given
        Integer toUserId = 9;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/friend/invite/{toUserId}", toUserId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.id").value(6));
        actions.andExpect(jsonPath("$.data.fromUser").value(1));
        actions.andExpect(jsonPath("$.data.toUser").value(3));
        actions.andExpect(jsonPath("$.data.status").value("대기"));


        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    @DisplayName("친구 요청 수락 성공")
    void friend_invite_accept_test() throws Exception {
        // given
        Integer inviteId = 2;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/friend/invite/{id}/accept", inviteId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.id").value(2));
        actions.andExpect(jsonPath("$.data.status").value("수락"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    // 본인의 초대가 아닌 초대 수락시 에러
    @Test
    void friend_invite_accept_fail_test() throws Exception {
        // given
        Integer inviteId = 6;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/friend/invite/{id}/accept", inviteId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isForbidden());
        actions.andExpect(jsonPath("$.msg").value("접근 권한이 없습니다."));
        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("친구 요청 거절 성공")
    void friend_invite_reject_test() throws Exception {
        // given
        Integer inviteId = 2;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/friend/invite/{id}/reject", inviteId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.id").value(2));
        actions.andExpect(jsonPath("$.data.status").value("거절"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    void friend_invite_reject_fail_test() throws Exception {
        // given
        Integer inviteId = 6;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/friend/invite/{id}/reject", inviteId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isForbidden());
        actions.andExpect(jsonPath("$.msg").value("접근 권한이 없습니다."));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}

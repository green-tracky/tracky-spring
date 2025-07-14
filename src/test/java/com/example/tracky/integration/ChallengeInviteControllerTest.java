package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky.community.challenges.dto.ChallengeInviteRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class ChallengeInviteControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    void challenges_invite_test() throws Exception {
        // given
        Integer challengeid = 1;

        List<Integer> inviteIds = Arrays.asList(3, 4, 5);

        ChallengeInviteRequest.InviteRequestDTO reqDTO = new ChallengeInviteRequest.InviteRequestDTO();
        reqDTO.setFriendIds(inviteIds);

        String requestBody = om.writeValueAsString(reqDTO);
        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/community/challenges/{id}/invite", challengeid)
                        .header("Authorization", "Bearer " + fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data[0].id").value(3));
        actions.andExpect(jsonPath("$.data[0].fromUser").value(1));
        actions.andExpect(jsonPath("$.data[0].toUser").value(3));
        actions.andExpect(jsonPath("$.data[0].challengeId").value(1));
        actions.andExpect(jsonPath("$.data[0].status").value("대기"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    // 친구가 아닌 사용자를 챌린지에 초대
    @Test
    void challenges_invite_fail_test() throws Exception {
        // given
        Integer challengeid = 1;

        List<Integer> inviteIds = Arrays.asList(2);

        ChallengeInviteRequest.InviteRequestDTO reqDTO = new ChallengeInviteRequest.InviteRequestDTO();
        reqDTO.setFriendIds(inviteIds);

        String requestBody = om.writeValueAsString(reqDTO);
        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/community/challenges/{id}/invite", challengeid)
                        .header("Authorization", "Bearer " + fakeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isNotFound());
        actions.andExpect(jsonPath("$.msg").value("서로 친구가 아닙니다"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
  
    @Test
    void get_available_friends_test() throws Exception {
        // given
        Integer challengeid = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/challenges/{id}/invite/available-friends", challengeid)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.[0].id").value(6));
        actions.andExpect(jsonPath("$.data.[0].profileUrl").value("http://example.com/profiles/leo.jpg"));
        actions.andExpect(jsonPath("$.data.[0].username").value("leo"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    // TODO : 본인이 참여한 챌린지가 아니라면 에러
    @Test
    void get_available_friends_fail_test() throws Exception {
        // given
        Integer challengeid = 3;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/challenges/{id}/invite/available-friends", challengeid)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.[0].id").value(6));
        actions.andExpect(jsonPath("$.data.[0].profileUrl").value("http://example.com/profiles/leo.jpg"));
        actions.andExpect(jsonPath("$.data.[0].username").value("leo"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    void friend_inviteAccept_test() throws Exception {
        // given
        Integer inviteId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/challenges/invite/{id}/accept", inviteId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.status").value("수락"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    // 본인의 초대가 아닌 것을 수락
    @Test
    void friend_inviteAccept_fail_test() throws Exception {
        // given
        Integer inviteId = 4;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/challenges/invite/{id}/accept", inviteId)
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
    void friend_inviteReject_test() throws Exception {
        // given
        Integer inviteId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/challenges/invite/{id}/reject", inviteId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.status").value("거절"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    // 본인의 초대가 아닌 것을 거절
    @Test
    void friend_inviteReject_fail_test() throws Exception {
        // given
        Integer inviteId = 4;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/challenges/invite/{id}/reject", inviteId)
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

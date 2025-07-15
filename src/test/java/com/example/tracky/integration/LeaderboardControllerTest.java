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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class LeaderboardControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    void get_leader_boards_week_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/leaderboards/week")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.totalDistanceMeters").value(3700));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.rank").value(1));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].totalDistanceMeters").value(3700));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].rank").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].userId").value(1));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    void get_leader_boards_week_before_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/leaderboards/week?before=1")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.totalDistanceMeters").value(9900));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.rank").value(1));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].totalDistanceMeters").value(9900));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].rank").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].userId").value(1));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    void get_leader_boards_mouth_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/leaderboards/mouth")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.totalDistanceMeters").value(17600));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.rank").value(1));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].totalDistanceMeters").value(17600));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].rank").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].userId").value(1));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    void get_leader_boards_mouth_before_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/leaderboards/mouth?before=1")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.totalDistanceMeters").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.rank").value(1));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].totalDistanceMeters").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].rank").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].userId").value(1));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    void get_leader_boards_year_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/leaderboards/year")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.totalDistanceMeters").value(17600));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.myRanking.rank").value(1));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].totalDistanceMeters").value(17600));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].rank").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.rankingList[0].userId").value(1));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
}

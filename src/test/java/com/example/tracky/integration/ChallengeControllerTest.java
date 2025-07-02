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

import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class ChallengeControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    @DisplayName("챌린지 목록 조회 성공")
    void get_challenges_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/challenges")
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data.recommendedChallenge 검증 랜덤이라 나중에 검증하기
//        actions.andExpect(jsonPath("$.data.recommendedChallenge.id").value(isA(Integer.class)));
//        actions.andExpect(jsonPath("$.data.recommendedChallenge.title").value("50K"));
//        actions.andExpect(jsonPath("$.data.recommendedChallenge.imageUrl").value("https://example.com/rewards/50km_badge.png"));
//        actions.andExpect(jsonPath("$.data.recommendedChallenge.participantCount").value(0));

        // data.myChallenges[0] 검증
        actions.andExpect(jsonPath("$.data.myChallenges[0].id").value(6));
        actions.andExpect(jsonPath("$.data.myChallenges[0].title").value("1K"));
        actions.andExpect(jsonPath("$.data.myChallenges[0].name").value("가볍게 1km 달리기"));
        actions.andExpect(jsonPath("$.data.myChallenges[0].sub").doesNotExist());
        actions.andExpect(jsonPath("$.data.myChallenges[0].remainingTime").value(isA(Number.class)));
        actions.andExpect(jsonPath("$.data.myChallenges[0].myDistance").value(1600));
        actions.andExpect(jsonPath("$.data.myChallenges[0].targetDistance").value(1000));
        actions.andExpect(jsonPath("$.data.myChallenges[0].isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.myChallenges[0].endDate").doesNotExist());

        // data.joinableChallenges 배열 크기 검증
        actions.andExpect(jsonPath("$.data.joinableChallenges.length()").value(4));

        // data.joinableChallenges[0] 일부 필드 검증 (대표 예시)
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].id").value(2));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].title").value("10K"));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].name").value("6월 10km 도전!"));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].sub").value("6월 한 달 동안 10km를 달성해보세요!"));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].remainingTime").value(isA(Number.class)));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].myDistance").doesNotExist());
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].targetDistance").doesNotExist());
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].endDate").doesNotExist());

        // data.pastChallenges[0] 검증
        actions.andExpect(jsonPath("$.data.pastChallenges[0].id").value(1));
        actions.andExpect(jsonPath("$.data.pastChallenges[0].title").value("5K"));
        actions.andExpect(jsonPath("$.data.pastChallenges[0].name").value("6월 주간 챌린지"));
        actions.andExpect(jsonPath("$.data.pastChallenges[0].sub").doesNotExist());
        actions.andExpect(jsonPath("$.data.pastChallenges[0].remainingTime").value(isA(Number.class)));
        actions.andExpect(jsonPath("$.data.pastChallenges[0].myDistance").value(3100));
        actions.andExpect(jsonPath("$.data.pastChallenges[0].targetDistance").value(5000));
        actions.andExpect(jsonPath("$.data.pastChallenges[0].isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.pastChallenges[0].endDate")
                .value(matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

// 디버깅 및 문서화 (필요시 활성화)
// actions.andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("챌린지 상세 조회 성공")
    void get_challenge_test() throws Exception {
        // given
        Integer challengeId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/challenges/{id}", challengeId)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
    }
}

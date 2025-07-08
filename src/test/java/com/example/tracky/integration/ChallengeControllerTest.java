package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
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
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // recommendedChallenge 존재 여부만 검증
        actions.andExpect(jsonPath("$.data.recommendedChallenge").exists());
        actions.andExpect(jsonPath("$.data.recommendedChallenge.id").isNumber());
        actions.andExpect(jsonPath("$.data.recommendedChallenge.name").isString());
        actions.andExpect(jsonPath("$.data.recommendedChallenge.imageUrl").isString());
        actions.andExpect(jsonPath("$.data.recommendedChallenge.participantCount").isNumber());
        actions.andExpect(jsonPath("$.data.recommendedChallenge.type").isString());

        // myChallenges[0]
        actions.andExpect(jsonPath("$.data.myChallenges[0].id").value(1));
        actions.andExpect(jsonPath("$.data.myChallenges[0].imageUrl").value("https://example.com/rewards/5km_badge.png"));
        actions.andExpect(jsonPath("$.data.myChallenges[0].name").value("6월 5k 챌린지"));
        actions.andExpect(jsonPath("$.data.myChallenges[0].sub").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.myChallenges[0].remainingTime").value(691199));
        actions.andExpect(jsonPath("$.data.myChallenges[0].myDistance").value(18100));
        actions.andExpect(jsonPath("$.data.myChallenges[0].targetDistance").value(5000));
        actions.andExpect(jsonPath("$.data.myChallenges[0].isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.myChallenges[0].endDate").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.myChallenges[0].type").value("공개"));

        // joinableChallenges[0]
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].id").value(2));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].imageUrl").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].name").value("6월 15k 챌린지"));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].sub").value("6월 한 달 동안 15km를 달성해보세요!"));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].remainingTime").value(691199));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].myDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].targetDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].endDate").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].type").value("공개"));

        // pastChallenges는 빈 배열이므로 size = 0 검증
        actions.andExpect(jsonPath("$.data.pastChallenges").isArray());
        actions.andExpect(jsonPath("$.data.pastChallenges").isEmpty());

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

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
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data 필드
        actions.andExpect(jsonPath("$.data.participantCount").value(4));
        actions.andExpect(jsonPath("$.data.myDistance").value(18100));
        actions.andExpect(jsonPath("$.data.isJoined").value(true));
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.name").value("6월 5k 챌린지"));
        actions.andExpect(jsonPath("$.data.sub").value("이번 주 5km를 달려보세요."));
        actions.andExpect(jsonPath("$.data.description").value("주간 챌린지를 통해 나의 한계를 뛰어넘어 보세요. 이번 주 5km를 달리면 특별한 완주자 기록을 달성할 수 있습니다."));
        actions.andExpect(jsonPath("$.data.startDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.endDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.targetDistance").value(5000));
        actions.andExpect(jsonPath("$.data.remainingTime").value(691199));
        actions.andExpect(jsonPath("$.data.isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.creatorName").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.type").value("공개"));
        actions.andExpect(jsonPath("$.data.rank").value(1));

        // rewards[0]
        actions.andExpect(jsonPath("$.data.rewards[0].rewardName").value("6월 5k 챌린지"));
        actions.andExpect(jsonPath("$.data.rewards[0].rewardImageUrl").value("https://example.com/rewards/participation.png"));
        actions.andExpect(jsonPath("$.data.rewards[0].status").value("달성"));
        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}

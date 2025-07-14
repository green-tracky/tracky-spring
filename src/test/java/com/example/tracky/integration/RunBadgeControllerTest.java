package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 컨트롤러 통합 테스트
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
public class RunBadgeControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om; // json <-> java Object 변환 해주는 객체. IoC에 objectMapper가 이미 떠있음

    @Test
    public void get_run_badges_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/run-badges")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // 🔸 data.recents[0]
        actions.andExpect(jsonPath("$.data.recents[0].id").value(1));
        actions.andExpect(jsonPath("$.data.recents[0].name").value("금메달"));
        actions.andExpect(jsonPath("$.data.recents[0].description").value("챌린지에서 1위를 달성하셨습니다!"));
        actions.andExpect(jsonPath("$.data.recents[0].imageUrl").value("https://example.com/rewards/gold.png"));
        actions.andExpect(jsonPath("$.data.recents[0].type").value("챌린지 수상자"));
        actions.andExpect(jsonPath("$.data.recents[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.recents[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.recents[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.recents[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.recents[0].isAchieved").value(true));
        actions.andExpect(jsonPath("$.data.recents[0].achievedCount").value(1));

        // 🔸 data.bests[0]
        actions.andExpect(jsonPath("$.data.bests[0].id").value(2));
        actions.andExpect(jsonPath("$.data.bests[0].name").value("1K 최고 기록"));
        actions.andExpect(jsonPath("$.data.bests[0].description").value("나의 1,000미터 최고 기록"));
        actions.andExpect(jsonPath("$.data.bests[0].imageUrl").value("https://example.com/badges/1k_best.png"));
        actions.andExpect(jsonPath("$.data.bests[0].type").value("최고기록"));
        actions.andExpect(jsonPath("$.data.bests[0].achievedAt").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.bests[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.bests[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.bests[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.bests[0].isAchieved").value(false));
        actions.andExpect(jsonPath("$.data.bests[0].achievedCount").value(Matchers.nullValue()));

        // 🔸 data.monthly[0]
        actions.andExpect(jsonPath("$.data.monthly[0].id").value(1));
        actions.andExpect(jsonPath("$.data.monthly[0].name").value("첫 시작"));
        actions.andExpect(jsonPath("$.data.monthly[0].description").value("매달 첫 러닝을 완료했어요!"));
        actions.andExpect(jsonPath("$.data.monthly[0].imageUrl").value("https://example.com/badges/first_run.png"));
        actions.andExpect(jsonPath("$.data.monthly[0].type").value("월간업적"));
        actions.andExpect(jsonPath("$.data.monthly[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.monthly[0].runRecordDistance").value(600));
        actions.andExpect(jsonPath("$.data.monthly[0].runRecordSeconds").value(270));
        actions.andExpect(jsonPath("$.data.monthly[0].runRecordPace").value(450));
        actions.andExpect(jsonPath("$.data.monthly[0].isAchieved").value(true));
        actions.andExpect(jsonPath("$.data.monthly[0].achievedCount").value(4));

        // 🔸 data.challenges[0]
        actions.andExpect(jsonPath("$.data.challenges[0].id").value(1));
        actions.andExpect(jsonPath("$.data.challenges[0].name").value("금메달"));
        actions.andExpect(jsonPath("$.data.challenges[0].description").value("챌린지에서 1위를 달성하셨습니다!"));
        actions.andExpect(jsonPath("$.data.challenges[0].imageUrl").value("https://example.com/rewards/gold.png"));
        actions.andExpect(jsonPath("$.data.challenges[0].type").value("챌린지 수상자"));
        actions.andExpect(jsonPath("$.data.challenges[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.challenges[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.challenges[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.challenges[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.challenges[0].isAchieved").value(true));
        actions.andExpect(jsonPath("$.data.challenges[0].achievedCount").value(1));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

}

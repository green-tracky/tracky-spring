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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
public class RunLevelControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om; // json <-> java Object 변환 해주는 객체. IoC에 objectMapper가 이미 떠있음

    @Test
    public void get_run_levels_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/run-levels")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // runLevels[0]
        actions.andExpect(jsonPath("$.data.runLevels[0].id").value(1));
        actions.andExpect(jsonPath("$.data.runLevels[0].name").value("옐로우"));
        actions.andExpect(jsonPath("$.data.runLevels[0].minDistance").value(0));
        actions.andExpect(jsonPath("$.data.runLevels[0].maxDistance").value(49999));
        actions.andExpect(jsonPath("$.data.runLevels[0].description").value("0 ~ 49.99킬로미터"));
        actions.andExpect(jsonPath("$.data.runLevels[0].sortOrder").value(0));
        actions.andExpect(jsonPath("$.data.runLevels[0].isCurrent").value(true));

        // totalDistance, distanceToNextLevel
        actions.andExpect(jsonPath("$.data.totalDistance").value(17600));
        actions.andExpect(jsonPath("$.data.distanceToNextLevel").value(32400));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

}

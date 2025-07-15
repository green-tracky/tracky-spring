package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky._core.enums.GenderEnum;
import com.example.tracky.user.UserRequest;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        UserRequest.IdTokenDTO reqDTO = new UserRequest.IdTokenDTO();
        reqDTO.setIdToken("idToken");

        String requestBody = om.writeValueAsString(reqDTO);
        log.debug("✅요청바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/api/oauth/kakao/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

// then: 응답 결과 검증
// HTTP 상태 코드가 200 (OK)인지 확인합니다.
        actions.andExpect(status().isOk());

// JSON 응답의 최상위 필드를 검증합니다.
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

// 'data' 객체 내부의 'user' 객체 필드들을 검증합니다.
        actions.andExpect(jsonPath("$.data.user.id").value(1));
        actions.andExpect(jsonPath("$.data.user.loginId").value("KAKAO_123456789"));
        actions.andExpect(jsonPath("$.data.user.username").value("ssar"));
        actions.andExpect(jsonPath("$.data.user.profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(jsonPath("$.data.user.height").value(175.0));
        actions.andExpect(jsonPath("$.data.user.weight").value(70.0));
        actions.andExpect(jsonPath("$.data.user.gender").value("남"));
        actions.andExpect(jsonPath("$.data.user.location").value("부산광역시"));
        actions.andExpect(jsonPath("$.data.user.letter").value("안녕하세요, 러닝을 사랑하는 ssar입니다."));
        actions.andExpect(jsonPath("$.data.user.provider").value("KAKAO"));
        actions.andExpect(jsonPath("$.data.user.userTag").value("#A1B2C3"));
        actions.andExpect(jsonPath("$.data.user.fcmToken").value("token_ssar_123"));

// createdAt 필드는 YYYY-MM-DD HH:MM:SS 형식인지 정규표현식으로 검증합니다.
        actions.andExpect(jsonPath("$.data.user.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

// 'data' 객체 내부의 'idToken' 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.idToken").value("idToken"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    @DisplayName("유저 정보 수정 성공")
    void update_test() throws Exception {
        // given
        Integer id = 1;

        UserRequest.UpdateDTO reqDTO = new UserRequest.UpdateDTO();
        reqDTO.setGender(GenderEnum.FEMALE);
        reqDTO.setHeight(185.0);
        reqDTO.setWeight(65.5);

        String requestBody = om.writeValueAsString(reqDTO);
        log.debug("✅요청바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/users/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

// then: 응답 결과 검증
// HTTP 상태 코드가 200 (OK)인지 확인합니다.
        actions.andExpect(status().isOk());

// JSON 응답의 최상위 필드를 검증합니다.
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

// 'data' 객체 내부의 사용자 정보 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.username").value("ssar"));
        actions.andExpect(jsonPath("$.data.profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(jsonPath("$.data.height").value(185.0));
        actions.andExpect(jsonPath("$.data.weight").value(65.5));
        actions.andExpect(jsonPath("$.data.gender").value("여"));
        actions.andExpect(jsonPath("$.data.location").value("부산광역시"));
        actions.andExpect(jsonPath("$.data.letter").value("안녕하세요, 러닝을 사랑하는 ssar입니다."));

// updatedAt 필드는 YYYY-MM-DD HH:MM:SS 형식인지 정규표현식으로 검증합니다.
        actions.andExpect(jsonPath("$.data.updatedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    void update_fail_test() throws Exception {
        // given
        Integer id = 2;

        UserRequest.UpdateDTO reqDTO = new UserRequest.UpdateDTO();
        reqDTO.setGender(GenderEnum.FEMALE);
        reqDTO.setHeight(185.0);
        reqDTO.setWeight(65.5);

        String requestBody = om.writeValueAsString(reqDTO);
        log.debug("✅요청바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/users/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

// then: 응답 결과 검증
// HTTP 상태 코드가 403 (Forbidden)인지 확인합니다.
        actions.andExpect(status().isForbidden());

// JSON 응답의 최상위 필드를 검증합니다.
        actions.andExpect(jsonPath("$.status").value(403));
        actions.andExpect(jsonPath("$.msg").value("접근 권한이 없습니다."));

// 'data' 필드가 null인지 확인합니다.
// import static org.hamcrest.Matchers.nullValue; 를 추가해야 합니다.
        actions.andExpect(jsonPath("$.data").value(nullValue()));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    @DisplayName("유저 정보 삭제 성공")
    void delete_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/users/{id}", id)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data").value(nullValue()));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    // 본인이 아닌 것 삭제
    @Test
    @DisplayName("유저 정보 삭제 실패")
    void delete_fail_test() throws Exception {
        // given
        Integer id = 10;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/users/{id}", id)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isForbidden());
        actions.andExpect(jsonPath("$.msg").value("접근 권한이 없습니다."));
        actions.andExpect(jsonPath("$.data").value(nullValue()));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    @DisplayName("유저 상세 성공")
    void get_user_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/{id}", id)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.loginId").value("KAKAO_123456789"));
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.username").value("ssar"));
        actions.andExpect(jsonPath("$.data.profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(jsonPath("$.data.height").value(175.0));
        actions.andExpect(jsonPath("$.data.weight").value(70.0));
        actions.andExpect(jsonPath("$.data.gender").value("남"));
        actions.andExpect(jsonPath("$.data.location").value("부산광역시"));
        actions.andExpect(jsonPath("$.data.letter").value("안녕하세요, 러닝을 사랑하는 ssar입니다."));
        actions.andExpect(jsonPath("$.data.userTag").value("#A1B2C3"));
        actions.andExpect(jsonPath("$.data.fcmToken").value("token_ssar_123"));

        actions.andExpect(jsonPath("$.data.runLevel.id").value(1));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));
        actions.andExpect(jsonPath("$.data.runLevel.minDistance").value(0));
        actions.andExpect(jsonPath("$.data.runLevel.maxDistance").value(49999));
        actions.andExpect(jsonPath("$.data.runLevel.description").value("0 ~ 49.99킬로미터"));
        actions.andExpect(jsonPath("$.data.runLevel.sortOrder").value(0));

        actions.andExpect(jsonPath("$.data.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.updatedAt").value(nullValue()));

        actions.andExpect(jsonPath("$.data.isOwner").value(true));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    // 사용자 상세보기
    @Test
    void get_user_fail_test() throws Exception {
        // given
        Integer id = 10;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/users/{id}", id)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isNotFound());
        actions.andExpect(jsonPath("$.msg").value("해당 사용자를 찾을 수 없습니다"));
        actions.andExpect(jsonPath("$.data").value(nullValue()));


        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
}

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

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.loginId").value("KAKAO_123456789"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("ssar@example.com"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.height").value(185.0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.weight").value(65.5));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.gender").value("여"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.location").value("부산광역시"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.letter").value("안녕하세요, 러닝을 사랑하는 ssar입니다."));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.provider").value("KAKAO"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.userTag").value("#A1B2C3"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.fcmToken").value("token_ssar_123"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.updatedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    // TODO : 본인 키 넣지 않음
    @Test
    void update_fail_test() throws Exception {
        // given
        Integer id = 1;

        UserRequest.UpdateDTO reqDTO = new UserRequest.UpdateDTO();
        reqDTO.setGender(GenderEnum.FEMALE);
//        reqDTO.setHeight(185.0);
        reqDTO.setWeight(65.5);

        String requestBody = om.writeValueAsString(reqDTO);
        log.debug("✅요청바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/user/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.status().isOk());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.loginId").value("KAKAO_123456789"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("ssar@example.com"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.height").value(185.0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.weight").value(65.5));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.gender").value("여"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.location").value("부산광역시"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.letter").value("안녕하세요, 러닝을 사랑하는 ssar입니다."));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.provider").value("KAKAO"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.userTag").value("#A1B2C3"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.flutterTokenId").value("token_ssar_123"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.updatedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

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
        actions.andExpect(MockMvcResultMatchers.status().isOk());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data").value(Matchers.nullValue()));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    // 본인이 아닌 것 삭제
    @Test
    @DisplayName("유저 정보 삭제 성공")
    void delete_fail_test() throws Exception {
        // given
        Integer id = 10;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/user/{id}", id)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.status().isForbidden());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("접근 권한이 없습니다."));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data").value(Matchers.nullValue()));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

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
        actions.andExpect(MockMvcResultMatchers.status().isOk());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("성공"));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.loginId").value("KAKAO_123456789"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("ssar"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("ssar@example.com"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.height").value(175.0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.weight").value(70.0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.gender").value("남"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.location").value("부산광역시"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.letter").value("안녕하세요, 러닝을 사랑하는 ssar입니다."));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.userTag").value("#A1B2C3"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.fcmToken").value("token_ssar_123"));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.runLevel.id").value(1));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.runLevel.name").value("옐로우"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.runLevel.minDistance").value(0));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.runLevel.maxDistance").value(49999));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.runLevel.description").value("0~49.99킬로미터"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.runLevel.imageUrl").value("https://example.com/images/yellow.png"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.runLevel.sortOrder").value(0));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.updatedAt").value(Matchers.nullValue()));

        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data.isOwner").value(true));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    // 사용자 상세보기
    @Test
    @DisplayName("유저 상세 성공")
    void get_user_fail_test() throws Exception {
        // given
        Integer id = 10;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/user/{id}", id)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(MockMvcResultMatchers.status().isNotFound());
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("해당 사용자를 찾을 수 없습니다"));
        actions.andExpect(MockMvcResultMatchers.jsonPath("$.data").value(Matchers.nullValue()));

        // then


        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }
}

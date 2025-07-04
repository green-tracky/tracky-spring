package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky.community.posts.PostRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class PostControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    @DisplayName("포스트 목록 조회 성공")
    void get_posts_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/posts")
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data 배열의 첫 번째 요소 검증
        actions.andExpect(jsonPath("$.data[0].likeCount").value(1));
        actions.andExpect(jsonPath("$.data[0].commentCount").value(1));
        actions.andExpect(jsonPath("$.data[0].isLiked").value(false));
        actions.andExpect(jsonPath("$.data[0].id").value(1));
        actions.andExpect(jsonPath("$.data[0].username").value("ssar"));
        actions.andExpect(jsonPath("$.data[0].content").value("ssar의 러닝 기록을 공유합니다."));
        actions.andExpect(jsonPath("$.data[0].createdAt").isNotEmpty());
        actions.andExpect(jsonPath("$.data[0].pictures").isArray());
        actions.andExpect(jsonPath("$.data[0].pictures").isEmpty());
    }

    @Test
    @DisplayName("포스트 쓰기 성공")
    void save_test() throws Exception {

        // given
        PostRequest.SaveDTO reqDTO = new PostRequest.SaveDTO();
        reqDTO.setContent("내용입니다");
        reqDTO.setRunRecordId(10);

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/community/posts")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data 내부 필드 검증
        actions.andExpect(jsonPath("$.data.id").isNumber());
        actions.andExpect(jsonPath("$.data.content").value("내용입니다"));
        actions.andExpect(jsonPath("$.data.userId").value(1));
        actions.andExpect(jsonPath("$.data.runRecordId").value(10));
        actions.andExpect(jsonPath("$.data.createdAt").isNotEmpty());

        // pictureIds가 빈 배열인지 확인
        actions.andExpect(jsonPath("$.data.pictureIds").isArray());
        actions.andExpect(jsonPath("$.data.pictureIds").isEmpty());

    }

    @Test
    @DisplayName("포스트 수정 성공")
    void update_test() throws Exception {

        // given
        int postId = 1;
        PostRequest.UpdateDTO reqDTO = new PostRequest.UpdateDTO();
        reqDTO.setContent("내용입니다");
        reqDTO.setRunRecordId(10);

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/posts/" + postId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data 내부 필드 검증
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.content").value("내용입니다"));
        actions.andExpect(jsonPath("$.data.runRecordId").value(10));
        actions.andExpect(jsonPath("$.data.createdAt").isNotEmpty());
        actions.andExpect(jsonPath("$.data.updatedAt").isNotEmpty());

        // pictureIds가 빈 배열인지 확인
        actions.andExpect(jsonPath("$.data.pictureIds").isArray());
        actions.andExpect(jsonPath("$.data.pictureIds").isEmpty());

    }
}

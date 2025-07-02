package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky.community.post.PostRequest;
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

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class PostControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    @DisplayName("포스트 목록 조회 성공")
    void get_posts_success() throws Exception {
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
    }

    @Test
    @DisplayName("포스트 쓰기 성공")
    void save_success() throws Exception {

        // given
        PostRequest.SaveDTO reqDTO = new PostRequest.SaveDTO();
        reqDTO.setTitle("제목입니다");
        reqDTO.setContent("내용입니다");
        reqDTO.setRunRecordId(1);

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
    }
}

package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
public class LikeControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em.createNativeQuery("alter table like_tb alter column id restart with 4").executeUpdate();
        em.createNativeQuery("alter table post_tb alter column id restart with 3").executeUpdate();
        em.createNativeQuery("alter table comment_tb alter column id restart with 28").executeUpdate();
    }

    @Test
    @DisplayName("게시글 좋아요 저장 성공")
    void save_post_test() throws Exception {

        // given
        Integer postId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/community/posts/{postId}/likes", postId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        //then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data.likeId").value(4));
        actions.andExpect(jsonPath("$.data.likeCount").value(2));

    }

    @Test
    @DisplayName("댓글 좋아요 저장 성공")
    void save_comment_test() throws Exception {

        // given
        Integer commentId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/community/comments/{commentId}/likes", commentId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        //then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data.likeId").value(4));
        actions.andExpect(jsonPath("$.data.likeCount").value(2));

    }

    @Test
    @DisplayName("삭제 성공 테스트")
    void delete_test() throws Exception {
        // given
        int postId = 1;
        int likeId = 3;

        //when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/community/posts/{postId}/likes/{likeId}", postId, likeId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        //then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data.likeCount").value(0));
    }
}

package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky.community.posts.comments.CommentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
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
public class CommentControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    @DisplayName("댓글 조회 성공")
    void get_comments_test() throws Exception {
        // given
        int postId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/posts/{postId}/comments", postId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then -> 댓글 완료 후 GPT 써서 작성

        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.current").value(1));
        actions.andExpect(jsonPath("$.data.totalCount").value(10));
        actions.andExpect(jsonPath("$.data.next").value(2));
        actions.andExpect(jsonPath("$.data.totalPage").value(5));
        actions.andExpect(jsonPath("$.data.isLast").value(false));

// comments[0] 검증
        actions.andExpect(jsonPath("$.data.comments[0].id").value(22));
        actions.andExpect(jsonPath("$.data.comments[0].postId").value(1));
        actions.andExpect(jsonPath("$.data.comments[0].userId").value(2));
        actions.andExpect(jsonPath("$.data.comments[0].username").value("cos"));
        actions.andExpect(jsonPath("$.data.comments[0].content").value("감동적인 글이었습니다."));
        actions.andExpect(jsonPath("$.data.comments[0].parentId").value(Matchers.nullValue()));

// 날짜 패턴 검증 (yyyy-MM-dd HH:mm:ss)
        actions.andExpect(jsonPath("$.data.comments[0].createdAt", Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.comments[0].updatedAt", Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

// children 검증
        actions.andExpect(jsonPath("$.data.comments[0].children").isArray());
        actions.andExpect(jsonPath("$.data.comments[0].children").isEmpty());

    }

    @Test
    @DisplayName("댓글 쓰기 성공")
    void save_test() throws Exception {

        // given
        CommentRequest.SaveDTO reqDTO = new CommentRequest.SaveDTO();
        reqDTO.setPostId(1);
        reqDTO.setContent("내용입니다");

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/community/posts/comments")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.id").value(28));
        actions.andExpect(jsonPath("$.data.postId").value(1));
        actions.andExpect(jsonPath("$.data.userId").value(1));
        actions.andExpect(jsonPath("$.data.username").value("ssar"));
        actions.andExpect(jsonPath("$.data.content").value("내용입니다"));
        actions.andExpect(jsonPath("$.data.parentId").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.createdAt").isNotEmpty());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void update_test() throws Exception {

        // given
        int postId = 1;
        int commentId = 3;

        CommentRequest.UpdateDTO reqDTO = new CommentRequest.UpdateDTO();
        reqDTO.setContent("수정된 내용입니다");

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/posts/{postId}/comments/{commentId}", postId, commentId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data.id").value(3));
        actions.andExpect(jsonPath("$.data.postId").value(1));
        actions.andExpect(jsonPath("$.data.userId").value(1));
        actions.andExpect(jsonPath("$.data.username").value("ssar"));
        actions.andExpect(jsonPath("$.data.content").value("수정된 내용입니다"));
        actions.andExpect(jsonPath("$.data.parentId").value((Object) null));
        actions.andExpect(jsonPath("$.data.updatedAt").isNotEmpty());
    }

    @Test
    @DisplayName("삭제 성공 테스트")
    void delete_test() throws Exception {
        // given
        int postId = 1;
        int commentId = 3;

        //when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/community/posts/{postId}/comments/{commentId}", postId, commentId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data").value((Object) null));
    }

}

package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky.community.posts.PostRepository;
import com.example.tracky.community.posts.comments.CommentRepository;
import com.example.tracky.community.posts.comments.CommentRequest;
import com.example.tracky.user.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글 조회 성공")
    void get_comments_test() throws Exception {
        // given
        int postId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/posts/{postId}/comments", postId)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then -> 댓글 완료 후 GPT 써서 작성

        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data[0].current").value(1));
        actions.andExpect(jsonPath("$.data[0].totalCount").value(10));
        actions.andExpect(jsonPath("$.data[0].id").value(22));
        actions.andExpect(jsonPath("$.data[0].postId").value(1));
        actions.andExpect(jsonPath("$.data[0].userId").value(2));
        actions.andExpect(jsonPath("$.data[0].username").value("cos"));
        actions.andExpect(jsonPath("$.data[0].content").value("감동적인 글이었습니다."));
        actions.andExpect(jsonPath("$.data[0].parentId").value(Matchers.nullValue())); // 또는 .doesNotExist()
        actions.andExpect(jsonPath("$.data[0].createdAt").isNotEmpty());
        actions.andExpect(jsonPath("$.data[0].updatedAt").isNotEmpty());
        actions.andExpect(jsonPath("$.data[0].children").isArray());
        actions.andExpect(jsonPath("$.data[0].children").isEmpty());
        actions.andExpect(jsonPath("$.data[0].prev").value(0));
        actions.andExpect(jsonPath("$.data[0].next").value(2));
        actions.andExpect(jsonPath("$.data[0].totalPage").value(5));
        actions.andExpect(jsonPath("$.data[0].isFirst").value(true));
        actions.andExpect(jsonPath("$.data[0].isLast").value(false));

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
                        .contentType(MediaType.APPLICATION_JSON));

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
        actions.andExpect(jsonPath("$.data.username").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.content").value("내용입니다"));
        actions.andExpect(jsonPath("$.data.parentId").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.createdAt").isNotEmpty());
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void update_test() throws Exception {

        // given
        int postId = 1;
        int commentId = 1;

        CommentRequest.UpdateDTO reqDTO = new CommentRequest.UpdateDTO();
        reqDTO.setContent("수정된 내용입니다");

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/posts/{postId}/comments/{commentId}", postId, commentId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.postId").value(1));
        actions.andExpect(jsonPath("$.data.userId").value(3));
        actions.andExpect(jsonPath("$.data.username").value("love"));
        actions.andExpect(jsonPath("$.data.content").value("수정된 내용입니다"));
        actions.andExpect(jsonPath("$.data.parentId").value((Object) null));
        actions.andExpect(jsonPath("$.data.updatedAt").isNotEmpty());
    }

}

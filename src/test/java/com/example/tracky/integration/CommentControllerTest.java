package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
public class CommentControllerTest extends MyRestDoc {

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
}

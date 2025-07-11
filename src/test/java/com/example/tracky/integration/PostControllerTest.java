package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky.community.posts.PostRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class PostControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("포스트 목록 조회 성공")
    void get_posts_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/posts")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data[0] 검증
        actions.andExpect(jsonPath("$.data[0].likeCount").value(1));
        actions.andExpect(jsonPath("$.data[0].commentCount").value(27));
        actions.andExpect(jsonPath("$.data[0].isLiked").value(false));
        actions.andExpect(jsonPath("$.data[0].id").value(1));
        actions.andExpect(jsonPath("$.data[0].username").value("ssar"));
        actions.andExpect(jsonPath("$.data[0].content").value("ssar의 러닝 기록을 공유합니다."));
        actions.andExpect(jsonPath("$.data[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
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

    @Test
    @DisplayName("삭제 성공 테스트")
    void delete_test() throws Exception {
        // given
        int postId = 1;

        //when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/community/posts/" + postId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data").value(nullValue()));

    }

    @Test
    @DisplayName("포스트 상세 조회 성공")
    void get_detail_test() throws Exception {
        // given
        int postId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/posts/" + postId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then 댓글까지 끝나면 나중에 작성
        // 최상위 응답
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data.comments 정보
        actions.andExpect(jsonPath("$.data.comments.current").value(1));
        actions.andExpect(jsonPath("$.data.comments.totalCount").value(10));
        actions.andExpect(jsonPath("$.data.comments.next").value(2));
        actions.andExpect(jsonPath("$.data.comments.totalPage").value(5));
        actions.andExpect(jsonPath("$.data.comments.isLast").value(false));

        // data.comments.comments (댓글 + 대댓글)
        actions.andExpect(jsonPath("$.data.comments.comments[0].id").value(22));
        actions.andExpect(jsonPath("$.data.comments.comments[0].postId").value(1));
        actions.andExpect(jsonPath("$.data.comments.comments[0].userId").value(2));
        actions.andExpect(jsonPath("$.data.comments.comments[0].username").value("cos"));
        actions.andExpect(jsonPath("$.data.comments.comments[0].content").value("감동적인 글이었습니다."));
        actions.andExpect(jsonPath("$.data.comments.comments[0].parentId").doesNotExist());
        actions.andExpect(jsonPath("$.data.comments.comments[0].children").isArray());

        // 좋아요 및 댓글 수
        actions.andExpect(jsonPath("$.data.likeCount").value(1));
        actions.andExpect(jsonPath("$.data.commentCount").value(27));
        actions.andExpect(jsonPath("$.data.isLiked").value(false));

        // 게시글 본문 정보
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.content").value("ssar의 러닝 기록을 공유합니다."));
        actions.andExpect(jsonPath("$.data.createdAt").isNotEmpty());
        actions.andExpect(jsonPath("$.data.updatedAt").isNotEmpty());

        // 작성자 정보
        actions.andExpect(jsonPath("$.data.user.id").value(1));
        actions.andExpect(jsonPath("$.data.user.username").value("ssar"));
        actions.andExpect(jsonPath("$.data.user.profileUrl").value("http://example.com/profiles/ssar.jpg"));

        // 러닝 기록
        actions.andExpect(jsonPath("$.data.runRecord.id").value(1));
        actions.andExpect(jsonPath("$.data.runRecord.title").value("부산 서면역 15번 출구 100m 러닝"));
        actions.andExpect(jsonPath("$.data.runRecord.memo").value("서면역 15번 출구에서 NC백화점 방향으로 100m 직선 러닝"));
        actions.andExpect(jsonPath("$.data.runRecord.calories").value(10));
        actions.andExpect(jsonPath("$.data.runRecord.totalDistanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.runRecord.totalDurationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.runRecord.elapsedTimeInSeconds").value(50));
        actions.andExpect(jsonPath("$.data.runRecord.avgPace").value(500));
        actions.andExpect(jsonPath("$.data.runRecord.bestPace").value(500));
        actions.andExpect(jsonPath("$.data.runRecord.userId").value(1));
        actions.andExpect(jsonPath("$.data.runRecord.createdAt").isNotEmpty());
        actions.andExpect(jsonPath("$.data.runRecord.intensity").value(3));
        actions.andExpect(jsonPath("$.data.runRecord.place").value("도로"));

        // 러닝 segments + coordinates 예시
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].id").value(1));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].startDate").isNotEmpty());
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].endDate").isNotEmpty());
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].durationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].distanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].pace").value(500));

        // 좌표 예시 (일부만)
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].coordinates[0].lat").value(35.1579));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].coordinates[0].lon").value(129.0594));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].coordinates[0].recordedAt").isNotEmpty());

        actions.andExpect(jsonPath("$.data.runRecord.segments[0].coordinates[1].lon").value(129.05944545));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].coordinates[25].lon").value(129.06053636));

        // pictures (빈 배열)
        actions.andExpect(jsonPath("$.data.pictures").isArray());
        actions.andExpect(jsonPath("$.data.pictures.length()").value(0));
    }

}



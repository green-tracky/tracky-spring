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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.hasSize;
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

// then: 응답 결과 검증
// HTTP 상태 코드가 200 (OK)인지 확인합니다.
        actions.andExpect(status().isOk());

// JSON 응답의 최상위 필드를 검증합니다.
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

// 'data' 배열의 크기가 2인지 확인합니다.
// import static org.hamcrest.Matchers.hasSize; 를 추가해야 합니다.
        actions.andExpect(jsonPath("$.data", hasSize(2)));

// 'data' 배열의 첫 번째 요소([0])의 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data[0].likeCount").value(1));
        actions.andExpect(jsonPath("$.data[0].commentCount").value(27));
        actions.andExpect(jsonPath("$.data[0].isLiked").value(false));
        actions.andExpect(jsonPath("$.data[0].id").value(1));
        actions.andExpect(jsonPath("$.data[0].content").value("ssar의 러닝 기록을 공유합니다."));

// 날짜 형식은 정규표현식으로 검증합니다.
        actions.andExpect(jsonPath("$.data[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

// 중첩된 'pictures' 배열을 검증합니다.
// 'pictures' 배열의 크기가 1인지 확인합니다.
        actions.andExpect(jsonPath("$.data[0].pictures", hasSize(1)));
        actions.andExpect(jsonPath("$.data[0].pictures[0].fileUrl").value("https://example.com/images/run1.jpg"));
        actions.andExpect(jsonPath("$.data[0].pictures[0].lat").value(37.5665));
        actions.andExpect(jsonPath("$.data[0].pictures[0].lon").value(126.978));
        actions.andExpect(jsonPath("$.data[0].pictures[0].savedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

// 중첩된 'user' 객체를 검증합니다.
        actions.andExpect(jsonPath("$.data[0].user.id").value(1));
        actions.andExpect(jsonPath("$.data[0].user.username").value("ssar"));
        actions.andExpect(jsonPath("$.data[0].user.profileUrl").value("http://example.com/profiles/ssar.jpg"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

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
        actions.andExpect(jsonPath("$.data.id").value(3));
        actions.andExpect(jsonPath("$.data.content").value("내용입니다"));
        actions.andExpect(jsonPath("$.data.userId").value(1));
        actions.andExpect(jsonPath("$.data.runRecordId").value(10));
        actions.andExpect(jsonPath("$.data.createdAt").value(
                Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        ));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    @DisplayName("포스트 쓰기 실패 - 존재하지 않는 RunRecordId")
    void save_fail_test() throws Exception {
        // given
        PostRequest.SaveDTO reqDTO = new PostRequest.SaveDTO();
        reqDTO.setContent("내용입니다");
        reqDTO.setRunRecordId(999); // 없는 ID

        String requestBody = om.writeValueAsString(reqDTO);

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
        log.debug("❌응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isNotFound());
        actions.andExpect(jsonPath("$.status").value(404));
        actions.andExpect(jsonPath("$.msg").value("해당 러닝을 찾을 수 없습니다"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("포스트 수정 성공")
    void update_test() throws Exception {

        // given
        int postId = 1;
        PostRequest.UpdateDTO reqDTO = new PostRequest.UpdateDTO();
        reqDTO.setContent("내용입니다");

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
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.content").value("내용입니다"));
        actions.andExpect(jsonPath("$.data.createdAt").<String>value(
                Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        ));
        actions.andExpect(jsonPath("$.data.updatedAt").<String>value(
                Matchers.matchesPattern("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")
        ));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("포스트 수정 실패 - 존재하지 않는 게시글")
    void update_fail_test() throws Exception {
        // given
        int invalidPostId = 999; // 존재하지 않는 ID
        PostRequest.UpdateDTO reqDTO = new PostRequest.UpdateDTO();
        reqDTO.setContent("내용입니다");

        String requestBody = om.writeValueAsString(reqDTO);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/posts/" + invalidPostId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("❌응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isNotFound());
        actions.andExpect(jsonPath("$.status").value(404));
        actions.andExpect(jsonPath("$.msg").value("해당 게시글을 찾을 수 없습니다"));
        actions.andExpect(jsonPath("$.data").doesNotExist());

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
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
        actions.andExpect(jsonPath("$.data").<Object>value(nullValue()));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    @DisplayName("삭제 실패 - 존재하지 않는 게시글")
    void delete_fail_test() throws Exception {
        // given
        int invalidPostId = 999;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/community/posts/" + invalidPostId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("❌응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isNotFound());
        actions.andExpect(jsonPath("$.status").value(404));
        actions.andExpect(jsonPath("$.msg").value("해당 게시글을 찾을 수 없습니다"));
        actions.andExpect(jsonPath("$.data").doesNotExist());

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
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

        // then -> 결과를 코드로 검증 // json의 최상위 객체는 $ 표기
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // 댓글 페이지 정보
        actions.andExpect(jsonPath("$.data.commentsInfo.current").value(1));
        actions.andExpect(jsonPath("$.data.commentsInfo.totalCount").value(10));
        actions.andExpect(jsonPath("$.data.commentsInfo.next").value(2));
        actions.andExpect(jsonPath("$.data.commentsInfo.totalPage").value(5));
        actions.andExpect(jsonPath("$.data.commentsInfo.isLast").value(false));

        // 댓글 0번
        actions.andExpect(jsonPath("$.data.commentsInfo.comments[0].id").value(22));
        actions.andExpect(jsonPath("$.data.commentsInfo.comments[0].postId").value(1));
        actions.andExpect(jsonPath("$.data.commentsInfo.comments[0].userId").value(2));
        actions.andExpect(jsonPath("$.data.commentsInfo.comments[0].username").value("cos"));
        actions.andExpect(jsonPath("$.data.commentsInfo.comments[0].content").value("감동적인 글이었습니다."));
        actions.andExpect(jsonPath("$.data.commentsInfo.comments[0].parentId").doesNotExist());
        actions.andExpect(jsonPath("$.data.commentsInfo.comments[0].createdAt").<String>value(Matchers.matchesRegex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(jsonPath("$.data.commentsInfo.comments[0].updatedAt").<String>value(Matchers.matchesRegex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(jsonPath("$.data.commentsInfo.comments[0].children").isArray());

        // 기타 정보
        actions.andExpect(jsonPath("$.data.likeCount").value(1));
        actions.andExpect(jsonPath("$.data.commentCount").value(27));
        actions.andExpect(jsonPath("$.data.isLiked").value(false));

        // 사용자 정보
        actions.andExpect(jsonPath("$.data.user.id").value(1));
        actions.andExpect(jsonPath("$.data.user.username").value("ssar"));
        actions.andExpect(jsonPath("$.data.user.profileUrl").value("http://example.com/profiles/ssar.jpg"));

        // 게시글 본문
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.content").value("ssar의 러닝 기록을 공유합니다."));

        // runRecord 기본 정보
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
        actions.andExpect(jsonPath("$.data.runRecord.createdAt").<String>value(Matchers.matchesRegex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(jsonPath("$.data.runRecord.intensity").value(3));
        actions.andExpect(jsonPath("$.data.runRecord.place").value("도로"));

        // runRecord.segments[0]
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].id").value(1));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].startDate").<String>value(Matchers.matchesRegex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].endDate").<String>value(Matchers.matchesRegex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].durationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].distanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].pace").value(500));

        // runRecord.segments[0].coordinates[0]
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].coordinates[0].lat").value(35.1579));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].coordinates[0].lon").value(129.0594));
        actions.andExpect(jsonPath("$.data.runRecord.segments[0].coordinates[0].recordedAt").<String>value(Matchers.matchesRegex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));

        // runRecord.pictures[0]
        actions.andExpect(jsonPath("$.data.runRecord.pictures[0].fileUrl").value("https://example.com/images/run1.jpg"));
        actions.andExpect(jsonPath("$.data.runRecord.pictures[0].lat").value(37.5665));
        actions.andExpect(jsonPath("$.data.runRecord.pictures[0].lon").value(126.978));
        actions.andExpect(jsonPath("$.data.runRecord.pictures[0].savedAt").<String>value(Matchers.matchesRegex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    @DisplayName("포스트 상세 조회 실패 - 존재하지 않는 게시글")
    void get_detail_fail_test() throws Exception {
        // given
        int invalidPostId = 999; // 존재하지 않는 게시글 ID

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/posts/" + invalidPostId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("❌응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isNotFound());
        actions.andExpect(jsonPath("$.status").value(404));
        actions.andExpect(jsonPath("$.msg").value("해당 게시글을 찾을 수 없습니다"));
        actions.andExpect(jsonPath("$.data").doesNotExist());

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }


}



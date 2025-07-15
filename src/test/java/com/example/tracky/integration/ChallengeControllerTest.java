package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky.community.challenges.dto.ChallengeRequest;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
class ChallengeControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om;

    @Test
    @DisplayName("챌린지 목록 조회 성공")
    void get_challenges_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/challenges")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
// 최상위 응답
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

// inviteChallenges[0]
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInviteId").value(1));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].fromUsername").value("leo"));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.id").value(1));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.name").value("6월 5k 챌린지"));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.sub").value("이번 주 5km를 달려보세요."));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.remainingTime").value(691199));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.myDistance").value(nullValue()));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.targetDistance").value(5000));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.startDate").value(Matchers.matchesRegex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.endDate").value(Matchers.matchesRegex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")));
        actions.andExpect(jsonPath("$.data.inviteChallenges[0].challengeInfo.type").value("공개"));

// recommendedChallenge
        actions.andExpect(jsonPath("$.data.recommendedChallenge.id").isNumber());
        actions.andExpect(jsonPath("$.data.recommendedChallenge.name").isString());
        actions.andExpect(jsonPath("$.data.recommendedChallenge.participantCount").isNumber());
        actions.andExpect(jsonPath("$.data.recommendedChallenge.type").isString());

// myChallenges[0]
        actions.andExpect(jsonPath("$.data.myChallenges[0].id").value(1));
        actions.andExpect(jsonPath("$.data.myChallenges[0].name").value("6월 5k 챌린지"));
        actions.andExpect(jsonPath("$.data.myChallenges[0].sub").value(nullValue()));
        actions.andExpect(jsonPath("$.data.myChallenges[0].remainingTime").value(691199));
        actions.andExpect(jsonPath("$.data.myChallenges[0].myDistance").value(17600));
        actions.andExpect(jsonPath("$.data.myChallenges[0].targetDistance").value(5000));
        actions.andExpect(jsonPath("$.data.myChallenges[0].isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.myChallenges[0].endDate").value(nullValue()));
        actions.andExpect(jsonPath("$.data.myChallenges[0].type").value("공개"));

// joinableChallenges[0]
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].id").value(2));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].name").value("6월 15k 챌린지"));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].sub").value("6월 한 달 동안 15km를 달성해보세요!"));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].remainingTime").value(691199));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].myDistance").value(nullValue()));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].targetDistance").value(nullValue()));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].endDate").value(nullValue()));
        actions.andExpect(jsonPath("$.data.joinableChallenges[0].type").value("공개"));

// pastChallenges
        actions.andExpect(jsonPath("$.data.pastChallenges").isArray());
        actions.andExpect(jsonPath("$.data.pastChallenges").isEmpty());

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    @DisplayName("챌린지 상세 조회 성공")
    void get_challenge_test() throws Exception {
        // given
        Integer challengeId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/challenges/{id}", challengeId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data 필드
        actions.andExpect(jsonPath("$.data.participantCount").value(4));
        actions.andExpect(jsonPath("$.data.myDistance").value(17600));
        actions.andExpect(jsonPath("$.data.isJoined").value(true));
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.name").value("6월 5k 챌린지"));
        actions.andExpect(jsonPath("$.data.sub").value("이번 주 5km를 달려보세요."));
        actions.andExpect(jsonPath("$.data.description").value("주간 챌린지를 통해 나의 한계를 뛰어넘어 보세요. 이번 주 5km를 달리면 특별한 완주자 기록을 달성할 수 있습니다."));
        actions.andExpect(jsonPath("$.data.startDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.endDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.targetDistance").value(5000));
        actions.andExpect(jsonPath("$.data.remainingTime").value(691199));
        actions.andExpect(jsonPath("$.data.isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.creatorName").value(nullValue()));
        actions.andExpect(jsonPath("$.data.type").value("공개"));
        actions.andExpect(jsonPath("$.data.rank").value(1));

        // rewards[0]
        actions.andExpect(jsonPath("$.data.rewards[0].rewardName").value("6월 5k 챌린지"));
        actions.andExpect(jsonPath("$.data.rewards[0].rewardImageUrl").value("https://example.com/rewards/participation.png"));
        actions.andExpect(jsonPath("$.data.rewards[0].status").value("달성"));
        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    // 없는 챌린지 조회
    @Test
    void get_challenge_fail_test() throws Exception {
        // given
        Integer challengeId = 10;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/challenges/{id}", challengeId)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isNotFound());
        actions.andExpect(jsonPath("$.msg").value("해당 챌린지를 찾을 수 없습니다"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    void save_test() throws Exception {
        // given
        // 1. 요청 DTO 생성
        ChallengeRequest.SaveDTO reqDTO = new ChallengeRequest.SaveDTO();
        reqDTO.setName("달리기 초보 모여라!");
        reqDTO.setTargetDistance(5000); // 목표: 5km
        reqDTO.setStartDate(LocalDateTime.of(2025, 7, 8, 0, 0));
        reqDTO.setEndDate(LocalDateTime.of(2025, 7, 15, 23, 59));
        reqDTO.setImgIndex(1);

        // 2. 요청 본문을 JSON 문자열로 변환
        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/community/challenges")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
// then: 응답 결과 검증
// HTTP 상태 코드가 200 (OK)인지 확인합니다.
        actions.andExpect(status().isOk());

// JSON 응답의 최상위 필드를 검증합니다.
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

// JSON 응답의 'data' 객체 내부 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.id").value(7));
        actions.andExpect(jsonPath("$.data.name").value("달리기 초보 모여라!"));

// 날짜 형식은 YYYY-MM-DD HH:MM:SS 패턴>>을 따르는지 정규표현식으로 검증합니다.
        actions.andExpect(jsonPath("$.data.startDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.endDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        actions.andExpect(jsonPath("$.data.targetDistance").value(5000));
        actions.andExpect(jsonPath("$.data.remainingTime").value(1987140));
        actions.andExpect(jsonPath("$.data.isInProgress").value(true));
        actions.andExpect(jsonPath("$.data.participantCount").value(1));
        actions.andExpect(jsonPath("$.data.creatorName").value("ssar"));
        actions.andExpect(jsonPath("$.data.type").value("사설"));
        actions.andExpect(jsonPath("$.data.isJoined").value(true));
        actions.andExpect(jsonPath("$.data.imgIndex").value(1));
        actions.andExpect(jsonPath("$.data.periodType").value("기타"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    void update_test() throws Exception {
        // given
        Integer id = 6;

        // 1. 요청 DTO 생성
        ChallengeRequest.UpdateDTO reqDTO = new ChallengeRequest.UpdateDTO();
        reqDTO.setName("달리기 초보 모여라!");

        // 2. 요청 본문을 JSON 문자열로 변환
        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/challenges/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then: 응답 결과 검증
// HTTP 상태 코드가 200 (OK)인지 확인합니다.
        actions.andExpect(status().isOk());

// JSON 응답의 최상위 필드를 검증합니다.
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

// 'data' 객체 내부의 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.id").value(6));
        actions.andExpect(jsonPath("$.data.name").value("달리기 초보 모여라!"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    void update_fail_test() throws Exception {
        // given
        Integer id = 1;

        // 1. 요청 DTO 생성
        ChallengeRequest.UpdateDTO reqDTO = new ChallengeRequest.UpdateDTO();
        reqDTO.setName("달리기 초보 모여라!");

        // 2. 요청 본문을 JSON 문자열로 변환
        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/community/challenges/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken));

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

    // 제목이 없는 챌린지 저장
    @Test
    void save_fail_test() throws Exception {
        // given
        // 1. 요청 DTO 생성
        ChallengeRequest.SaveDTO reqDTO = new ChallengeRequest.SaveDTO();

//        reqDTO.setName("달리기 초보 모여라!");
        reqDTO.setTargetDistance(5000); // 목표: 5km
        reqDTO.setStartDate(LocalDateTime.of(2025, 7, 8, 0, 0));
        reqDTO.setEndDate(LocalDateTime.of(2025, 7, 15, 23, 59));
        reqDTO.setImgIndex(2);

        // 2. 요청 본문을 JSON 문자열로 변환
        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/community/challenges")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isBadRequest());
        actions.andExpect(jsonPath("$.status").value(400));
        actions.andExpect(jsonPath("$.msg").value("name : 챌린지 이름은 필수 입력 항목입니다."));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);

    }

    @Test
    void get_challenge_leader_board_test() throws Exception {
        // given
        Integer challengeId = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/community/challenges/{id}/leaderboard", challengeId)
                        .header("Authorization", "Bearer " + fakeToken));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.rankingList[0].profileUrl").value("http://example.com/profiles/ssar.jpg"));
        actions.andExpect(jsonPath("$.data.rankingList[0].username").value("ssar"));
        actions.andExpect(jsonPath("$.data.rankingList[0].totalDistanceMeters").value(17600));
        actions.andExpect(jsonPath("$.data.rankingList[0].rank").value(1));
        actions.andExpect(jsonPath("$.data.rankingList[0].userId").value(1));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}

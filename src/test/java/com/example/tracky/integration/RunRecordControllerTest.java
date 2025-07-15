package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky._core.enums.RunPlaceTypeEnum;
import com.example.tracky.runrecord.RunRecordRequest;
import com.example.tracky.runrecord.pictures.PictureRequest;
import com.example.tracky.runrecord.runsegments.RunSegmentRequest;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 컨트롤러 통합 테스트
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
public class RunRecordControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om; // json <-> java Object 변환 해주는 객체. IoC에 objectMapper가 이미 떠있음

    // ⭐️ [수정 1] 날짜/시간 포맷을 미리 정의해두면 재사용하기 편리합니다.
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void save_test() throws Exception {
        // given

        // 러닝 생성
        RunRecordRequest.SaveDTO reqDTO = new RunRecordRequest.SaveDTO();
        reqDTO.setTitle("부산 해운대 아침 달리기");
        reqDTO.setCalories(200);

        // 구간 생성
        List<RunSegmentRequest.DTO> segments = new ArrayList<>();

        RunSegmentRequest.DTO segment1 = new RunSegmentRequest.DTO();
        segment1.setStartDate(LocalDateTime.parse("2025-06-22 06:30:00", formatter));
        segment1.setEndDate(LocalDateTime.parse("2025-06-22 06:37:10", formatter));
        segment1.setDurationSeconds(430);
        segment1.setDistanceMeters(1000);

        List<RunCoordinateRequest.DTO> coordinates1 = new ArrayList<>();
        RunCoordinateRequest.DTO coord1 = new RunCoordinateRequest.DTO();
        coord1.setLat(35.1587);
        coord1.setLon(129.1604);
        coord1.setRecordedAt(LocalDateTime.parse("2025-06-22 06:30:00", formatter));
        coordinates1.add(coord1);

        RunCoordinateRequest.DTO coord2 = new RunCoordinateRequest.DTO();
        coord2.setLat(35.1595);
        coord2.setLon(129.1612);
        coord2.setRecordedAt(LocalDateTime.parse("2025-06-22 06:33:45", formatter));
        coordinates1.add(coord2);

        RunCoordinateRequest.DTO coord3 = new RunCoordinateRequest.DTO();
        coord3.setLat(35.1602);
        coord3.setLon(129.1620);
        coord3.setRecordedAt(LocalDateTime.parse("2025-06-22 06:37:10", formatter));
        coordinates1.add(coord3);

        segment1.setCoordinates(coordinates1);
        segments.add(segment1);

        RunSegmentRequest.DTO segment2 = new RunSegmentRequest.DTO();
        segment2.setStartDate(LocalDateTime.parse("2025-06-22 06:37:11", formatter));
        segment2.setEndDate(LocalDateTime.parse("2025-06-22 06:43:05", formatter));
        segment2.setDurationSeconds(354);
        segment2.setDistanceMeters(1000);

        List<RunCoordinateRequest.DTO> coordinates2 = new java.util.ArrayList<>();
        RunCoordinateRequest.DTO coord4 = new RunCoordinateRequest.DTO();
        coord4.setLat(35.1610);
        coord4.setLon(129.1628);
        coord4.setRecordedAt(LocalDateTime.parse("2025-06-22 06:40:00", formatter));
        coordinates2.add(coord4);

        RunCoordinateRequest.DTO coord5 = new RunCoordinateRequest.DTO();
        coord5.setLat(35.1618);
        coord5.setLon(129.1635);
        coord5.setRecordedAt(LocalDateTime.parse("2025-06-22 06:43:05", formatter));
        coordinates2.add(coord5);

        segment2.setCoordinates(coordinates2);
        segments.add(segment2);

        reqDTO.setSegments(segments);

        List<PictureRequest.DTO> pictures = new ArrayList<>();
        PictureRequest.DTO picture1 = new PictureRequest.DTO();
        picture1.setFileUrl("http://example.com/profiles/cos.jpg");
        picture1.setLat(35.1598);
        picture1.setLon(129.1615);
        picture1.setSavedAt(LocalDateTime.parse("2025-06-22 06:43:05", formatter));
        pictures.add(picture1);

        reqDTO.setPictures(pictures);

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/runs")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
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

// 'data' 객체 내부의 기본 필드들을 검증합니다.
        actions.andExpect(jsonPath("$.data.id").value(17));
        actions.andExpect(jsonPath("$.data.title").value("부산 해운대 아침 달리기"));
        actions.andExpect(jsonPath("$.data.calories").value(200));
        actions.andExpect(jsonPath("$.data.totalDistanceMeters").value(2000));
        actions.andExpect(jsonPath("$.data.totalDurationSeconds").value(784));
        actions.andExpect(jsonPath("$.data.avgPace").value(392));
        actions.andExpect(jsonPath("$.data.bestPace").value(354));
        actions.andExpect(jsonPath("$.data.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.userId").value(1));

// 'data.segments' 배열을 검증합니다. (크기: 2)
// import static org.hamcrest.Matchers.hasSize; 를 추가해야 합니다.
        actions.andExpect(jsonPath("$.data.segments", hasSize(2)));

// 'data.segments' 배열의 첫 번째 요소([0]) 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.segments[0].id").value(32));
        actions.andExpect(jsonPath("$.data.segments[0].startDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].endDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].durationSeconds").value(430));
        actions.andExpect(jsonPath("$.data.segments[0].distanceMeters").value(1000));
        actions.andExpect(jsonPath("$.data.segments[0].pace").value(430));

// 'segments[0].coordinates' 배열을 검증합니다. (크기: 3)
        actions.andExpect(jsonPath("$.data.segments[0].coordinates", hasSize(3)));

// 'coordinates' 배열의 첫 번째 요소([0]) 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lat").value(35.1587));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lon").value(129.1604));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].recordedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

// 'data.pictures' 배열을 검증합니다. (크기: 1)
        actions.andExpect(jsonPath("$.data.pictures", hasSize(1)));

// 'pictures' 배열의 첫 번째 요소([0]) 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.pictures[0].fileUrl").value("http://example.com/profiles/cos.jpg"));
        actions.andExpect(jsonPath("$.data.pictures[0].lat").value(35.1598));
        actions.andExpect(jsonPath("$.data.pictures[0].lon").value(129.1615));
        actions.andExpect(jsonPath("$.data.pictures[0].savedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

// 'data.badges' 배열을 검증합니다. (크기: 1)
        actions.andExpect(jsonPath("$.data.badges", hasSize(1)));

// 'badges' 배열의 첫 번째 요소([0]) 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.badges[0].id").value(2));
        actions.andExpect(jsonPath("$.data.badges[0].name").value("1K 최고 기록"));
        actions.andExpect(jsonPath("$.data.badges[0].description").value("나의 1,000미터 최고 기록"));
        actions.andExpect(jsonPath("$.data.badges[0].imageUrl").value("https://example.com/badges/1k_best.png"));
        actions.andExpect(jsonPath("$.data.badges[0].type").value("최고기록"));
        actions.andExpect(jsonPath("$.data.badges[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.badges[0].runRecordDistance").value(2000));
        actions.andExpect(jsonPath("$.data.badges[0].runRecordSeconds").value(784));
        actions.andExpect(jsonPath("$.data.badges[0].runRecordPace").value(392));
        actions.andExpect(jsonPath("$.data.badges[0].isAchieved").value(true));
// import static org.hamcrest.Matchers.nullValue; 를 추가해야 합니다.
        actions.andExpect(jsonPath("$.data.badges[0].achievedCount").value(nullValue()));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    // TODO : 제목 없을 시 오류
    @Test
    public void save_fail_test() throws Exception {
        // given

        // 러닝 생성
        RunRecordRequest.SaveDTO reqDTO = new RunRecordRequest.SaveDTO();
//        reqDTO.setTitle("부산 해운대 아침 달리기");
        reqDTO.setCalories(200);

        // 구간 생성
        List<RunSegmentRequest.DTO> segments = new ArrayList<>();

        RunSegmentRequest.DTO segment1 = new RunSegmentRequest.DTO();
        segment1.setStartDate(LocalDateTime.parse("2025-06-22 06:30:00", formatter));
        segment1.setEndDate(LocalDateTime.parse("2025-06-22 06:37:10", formatter));
        segment1.setDurationSeconds(430);
        segment1.setDistanceMeters(1000);

        List<RunCoordinateRequest.DTO> coordinates1 = new ArrayList<>();
        RunCoordinateRequest.DTO coord1 = new RunCoordinateRequest.DTO();
        coord1.setLat(35.1587);
        coord1.setLon(129.1604);
        coord1.setRecordedAt(LocalDateTime.parse("2025-06-22 06:30:00", formatter));
        coordinates1.add(coord1);

        RunCoordinateRequest.DTO coord2 = new RunCoordinateRequest.DTO();
        coord2.setLat(35.1595);
        coord2.setLon(129.1612);
        coord2.setRecordedAt(LocalDateTime.parse("2025-06-22 06:33:45", formatter));
        coordinates1.add(coord2);

        RunCoordinateRequest.DTO coord3 = new RunCoordinateRequest.DTO();
        coord3.setLat(35.1602);
        coord3.setLon(129.1620);
        coord3.setRecordedAt(LocalDateTime.parse("2025-06-22 06:37:10", formatter));
        coordinates1.add(coord3);

        segment1.setCoordinates(coordinates1);
        segments.add(segment1);

        RunSegmentRequest.DTO segment2 = new RunSegmentRequest.DTO();
        segment2.setStartDate(LocalDateTime.parse("2025-06-22 06:37:11", formatter));
        segment2.setEndDate(LocalDateTime.parse("2025-06-22 06:43:05", formatter));
        segment2.setDurationSeconds(354);
        segment2.setDistanceMeters(1000);

        List<RunCoordinateRequest.DTO> coordinates2 = new java.util.ArrayList<>();
        RunCoordinateRequest.DTO coord4 = new RunCoordinateRequest.DTO();
        coord4.setLat(35.1610);
        coord4.setLon(129.1628);
        coord4.setRecordedAt(LocalDateTime.parse("2025-06-22 06:40:00", formatter));
        coordinates2.add(coord4);

        RunCoordinateRequest.DTO coord5 = new RunCoordinateRequest.DTO();
        coord5.setLat(35.1618);
        coord5.setLon(129.1635);
        coord5.setRecordedAt(LocalDateTime.parse("2025-06-22 06:43:05", formatter));
        coordinates2.add(coord5);

        segment2.setCoordinates(coordinates2);
        segments.add(segment2);

        reqDTO.setSegments(segments);

        List<PictureRequest.DTO> pictures = new ArrayList<>();
        PictureRequest.DTO picture1 = new PictureRequest.DTO();
        picture1.setFileUrl("http://example.com/profiles/cos.jpg");
        picture1.setLat(35.1598);
        picture1.setLon(129.1615);
        picture1.setSavedAt(LocalDateTime.parse("2025-06-22 06:43:05", formatter));
        pictures.add(picture1);

        reqDTO.setPictures(pictures);

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/runs")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then: 응답 결과 검증
// HTTP 상태 코드가 400 (Bad Request)인지 확인합니다.
        actions.andExpect(status().isBadRequest());

// JSON 응답의 최상위 필드를 검증합니다.
        actions.andExpect(jsonPath("$.status").value(400));
        actions.andExpect(jsonPath("$.msg").value("title : 제목은 필수 입력 항목입니다."));

// 'data' 필드가 null인지 확인합니다.
// import static org.hamcrest.Matchers.nullValue; 를 추가해야 합니다.
        actions.andExpect(jsonPath("$.data").value(nullValue()));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_run_record_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/runs/{id}", id)
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

// 'data' 객체 내부의 기본 필드들을 검증합니다.
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.title").value("부산 서면역 15번 출구 100m 러닝"));
        actions.andExpect(jsonPath("$.data.memo").value("서면역 15번 출구에서 NC백화점 방향으로 100m 직선 러닝"));
        actions.andExpect(jsonPath("$.data.calories").value(10));
        actions.andExpect(jsonPath("$.data.totalDistanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.totalDurationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.elapsedTimeInSeconds").value(50));
        actions.andExpect(jsonPath("$.data.avgPace").value(500));
        actions.andExpect(jsonPath("$.data.bestPace").value(500));
        actions.andExpect(jsonPath("$.data.userId").value(1));
        actions.andExpect(jsonPath("$.data.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.intensity").value(3));
        actions.andExpect(jsonPath("$.data.place").value("도로"));

// 'data.segments' 배열을 검증합니다.
// import static org.hamcrest.Matchers.hasSize; 를 추가해야 합니다.
        actions.andExpect(jsonPath("$.data.segments", hasSize(1)));

// 'data.segments' 배열의 첫 번째 요소([0]) 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.segments[0].id").value(1));
        actions.andExpect(jsonPath("$.data.segments[0].startDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].endDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].durationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.segments[0].distanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.segments[0].pace").value(500));

// 'segments[0].coordinates' 배열을 검증합니다. (총 26개)
        actions.andExpect(jsonPath("$.data.segments[0].coordinates", hasSize(26)));

// 'coordinates' 배열의 첫 번째 요소([0]) 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lat").value(35.1579));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lon").value(129.0594));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].recordedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

// 'data.pictures' 배열을 검증합니다.
        actions.andExpect(jsonPath("$.data.pictures", hasSize(1)));

// 'pictures' 배열의 첫 번째 요소([0]) 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.pictures[0].fileUrl").value("https://example.com/images/run1.jpg"));
        actions.andExpect(jsonPath("$.data.pictures[0].lat").value(37.5665));
        actions.andExpect(jsonPath("$.data.pictures[0].lon").value(126.978));
        actions.andExpect(jsonPath("$.data.pictures[0].savedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));


        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void delete_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/runs/{id}", id)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data").value(nullValue())); // data 필드가 null인지 검증

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void delete_fail_test() throws Exception {
        // given
        Integer id = 111;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/runs/{id}", id)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isNotFound());
        actions.andExpect(jsonPath("$.msg").value("해당 러닝을 찾을 수 없습니다"));
        actions.andExpect(jsonPath("$.data").value(nullValue())); // data 필드가 null인지 검증

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void update_fail_test() throws Exception {
        // given
        Integer id = 2;
        RunRecordRequest.UpdateDTO reqDTO = new RunRecordRequest.UpdateDTO();
        reqDTO.setTitle("수정 확인");
        reqDTO.setMemo("수정 확인");
        reqDTO.setPlace(RunPlaceTypeEnum.TRACK);
        reqDTO.setIntensity(1);

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/runs/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then: 응답 결과 검증
        actions.andExpect(status().isForbidden());
        actions.andExpect(jsonPath("$.msg").value("접근 권한이 없습니다."));
        actions.andExpect(jsonPath("$.status").value(403));
        actions.andExpect(jsonPath("$.data").value(Matchers.nullValue()));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void update_test() throws Exception {
        // given
        Integer id = 1;
        RunRecordRequest.UpdateDTO reqDTO = new RunRecordRequest.UpdateDTO();
        reqDTO.setTitle("수정 확인");
        reqDTO.setMemo("수정 확인");
        reqDTO.setPlace(RunPlaceTypeEnum.TRACK);
        reqDTO.setIntensity(1);

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .put("/s/api/runs/{id}", id)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
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

// 'data' 객체 내부의 필드를 검증합니다.
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.title").value("수정 확인"));
        actions.andExpect(jsonPath("$.data.memo").value("수정 확인"));
        actions.andExpect(jsonPath("$.data.intensity").value(1));
        actions.andExpect(jsonPath("$.data.place").value("트랙"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_week_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/week")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // 🔹 avgStats
        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(2));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(348));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(7400));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(2580));

        // 🔹 achievementHistory[0]
        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("챌린지 수상자"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("금메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].description").value("챌린지에서 1위를 달성하셨습니다!"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/gold.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedCount").value(1));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(true));

        // 🔹 recentRuns[0]
        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(16));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1900));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(660));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(347));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges").isArray());
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        // 🔹 runLevel
        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(17600));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(32400));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));

        // 🔹 weeks.2025-06
        actions.andExpect(jsonPath("$.data.weeks['2025-06'][0]").value("06.09~06.15"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_week_before_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/week?before=1")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // 🔹 avgStats
        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(8));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(365));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(19800));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(7240));

        // 🔹 achievementHistory[0]
        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("챌린지 수상자"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("금메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].description").value("챌린지에서 1위를 달성하셨습니다!"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/gold.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedCount").value(1));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(true));

        // 🔹 recentRuns[0]
        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(16));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1900));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(660));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(347));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges").isArray());
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        // 🔹 runLevel
        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(17600));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(32400));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));

        // 🔹 weeks.2025-06
        actions.andExpect(jsonPath("$.data.weeks['2025-06'][0]").value("06.09~06.15"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_month_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/month")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // 🔹 avgStats
        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(15));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(35200));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(13120));

        // 🔹 achievementHistory[0]
        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("챌린지 수상자"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("금메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].description").value("챌린지에서 1위를 달성하셨습니다!"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/gold.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedCount").value(1));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(true));

        // 🔹 recentRuns[0]
        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(16));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1900));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(660));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(347));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges").isArray());
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        // 🔹 runLevel
        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(17600));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(32400));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));

        // 🔹 years
        actions.andExpect(jsonPath("$.data.years[0]").value(2025));

        // 🔹 mounts
        actions.andExpect(jsonPath("$.data.mounts['2025'][0]").value(6));


        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_month_param_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/month?month=6&year=2025")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // 🔹 avgStats
        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(15));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(35200));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(13120));

        // 🔹 achievementHistory[0]
        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("챌린지 수상자"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("금메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].description").value("챌린지에서 1위를 달성하셨습니다!"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/gold.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedCount").value(1));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(true));

        // 🔹 recentRuns[0]
        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(16));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1900));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(660));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(347));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges").isArray());
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        // 🔹 runLevel
        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(17600));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(32400));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));

        // 🔹 years
        actions.andExpect(jsonPath("$.data.years[0]").value(2025));

        // 🔹 mounts
        actions.andExpect(jsonPath("$.data.mounts['2025'][0]").value(6));


        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_year_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/year")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // avgStats
        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(15));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(35200));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(13120));

        // totalStats
        actions.andExpect(jsonPath("$.data.totalStats.runCountPerWeek").value(0.2));
        actions.andExpect(jsonPath("$.data.totalStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.totalStats.avgDistanceMetersPerRun").value(1173));
        actions.andExpect(jsonPath("$.data.totalStats.avgDurationSecondsPerRun").value(437));

        // achievementHistory[0]
        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("챌린지 수상자"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("금메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].description").value("챌린지에서 1위를 달성하셨습니다!"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/gold.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedCount").value(1));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(true));

        // recentRuns[0]
        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(16));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1900));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(660));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(347));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges").isArray());
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        // runLevel
        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(17600));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(32400));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));

        // years
        actions.andExpect(jsonPath("$.data.years[0]").value(2025));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_year_param_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/year?year=2025")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // avgStats
        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(15));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(35200));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(13120));

        // totalStats
        actions.andExpect(jsonPath("$.data.totalStats.runCountPerWeek").value(0.2));
        actions.andExpect(jsonPath("$.data.totalStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.totalStats.avgDistanceMetersPerRun").value(1173));
        actions.andExpect(jsonPath("$.data.totalStats.avgDurationSecondsPerRun").value(437));

        // achievementHistory[0]
        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("챌린지 수상자"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("금메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].description").value("챌린지에서 1위를 달성하셨습니다!"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/gold.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedCount").value(1));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(true));

        // recentRuns[0]
        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(16));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1900));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(660));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(347));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges").isArray());
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        // runLevel
        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(17600));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(32400));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));

        // years
        actions.andExpect(jsonPath("$.data.years[0]").value(2025));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_all_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/all")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // 🔹 avgStats
        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(15));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(17600));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(6560));

        // 🔹 totalStats
        actions.andExpect(jsonPath("$.data.totalStats.runCountPerWeek").value(5.0));
        actions.andExpect(jsonPath("$.data.totalStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.totalStats.avgDistanceMetersPerRun").value(1173));
        actions.andExpect(jsonPath("$.data.totalStats.avgDurationSecondsPerRun").value(437));

        // 🔹 achievementHistory[0]
        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("챌린지 수상자"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("금메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].description").value("챌린지에서 1위를 달성하셨습니다!"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/gold.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedCount").value(1));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(Matchers.nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(true));

        // 🔹 recentRuns[0]
        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(16));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1900));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(660));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(347));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2}.*")));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges").isArray());
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        // 🔹 runLevel
        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(17600));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(32400));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_recent_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/recent")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // groupedRecentList[0]
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].yearMonth").value("2025-06-01 00:00:00"));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].avgStats.recodeCount").value(15));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].avgStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].avgStats.totalDistanceMeters").value(17600));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].avgStats.totalDurationSeconds").value(6560));

        // recentRuns[4] : "부산 서면역 15번 출구 100m 러닝"
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[4].id").value(1));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[4].title").value("부산 서면역 15번 출구 100m 러닝"));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[4].totalDistanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[4].totalDurationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[4].avgPace").value(500));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[4].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // badges[0]
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[4].badges[0].id").value(1));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[4].badges[0].name").value("첫 시작"));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[4].badges[0].imageUrl").value("https://example.com/badges/first_run.png"));

        // page 정보
        actions.andExpect(jsonPath("$.data.page.totalCount").value(1));
        actions.andExpect(jsonPath("$.data.page.current").value(1));
        actions.andExpect(jsonPath("$.data.page.size").value(3));
        actions.andExpect(jsonPath("$.data.page.totalPage").value(1));
        actions.andExpect(jsonPath("$.data.page.isFirst").value(true));
        actions.andExpect(jsonPath("$.data.page.isLast").value(true));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_recent_oldest_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/recent?order=oldest")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // groupedRecentList[0]
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].yearMonth").value("2025-06-01 00:00:00"));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].avgStats.recodeCount").value(15));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].avgStats.avgPace").value(372));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].avgStats.totalDistanceMeters").value(17600));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].avgStats.totalDurationSeconds").value(6560));

        // recentRuns[4] : "부산 서면역 15번 출구 100m 러닝"
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[0].id").value(3));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[0].title").value("두번째 러닝 2"));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[0].totalDistanceMeters").value(600));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[0].totalDurationSeconds").value(270));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[0].avgPace").value(450));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // badges[0]
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[0].badges[0].id").value(1));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[0].badges[0].name").value("첫 시작"));
        actions.andExpect(jsonPath("$.data.groupedRecentList[0].recentRuns[0].badges[0].imageUrl").value("https://example.com/badges/first_run.png"));

        // page 정보
        actions.andExpect(jsonPath("$.data.page.totalCount").value(1));
        actions.andExpect(jsonPath("$.data.page.current").value(1));
        actions.andExpect(jsonPath("$.data.page.size").value(3));
        actions.andExpect(jsonPath("$.data.page.totalPage").value(1));
        actions.andExpect(jsonPath("$.data.page.isFirst").value(true));
        actions.andExpect(jsonPath("$.data.page.isLast").value(true));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_recent_distance_desc_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/recent?order=distance-desc")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.recentList[0].id").value("16"));
        actions.andExpect(jsonPath("$.data.recentList[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data.recentList[0].totalDistanceMeters").value(1900));
        actions.andExpect(jsonPath("$.data.recentList[0].totalDurationSeconds").value(660));
        actions.andExpect(jsonPath("$.data.recentList[0].avgPace").value(347));
        actions.andExpect(jsonPath("$.data.recentList[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.recentList[0].badges.length()").value(0));

        // page 정보
        actions.andExpect(jsonPath("$.data.page.totalCount").value(15));
        actions.andExpect(jsonPath("$.data.page.current").value(1));
        actions.andExpect(jsonPath("$.data.page.size").value(3));
        actions.andExpect(jsonPath("$.data.page.totalPage").value(5));
        actions.andExpect(jsonPath("$.data.page.isFirst").value(true));
        actions.andExpect(jsonPath("$.data.page.isLast").value(false));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_recent_distance_asc_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/recent?order=distance-asc")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.recentList[0].id").value(1));
        actions.andExpect(jsonPath("$.data.recentList[0].title").value("부산 서면역 15번 출구 100m 러닝"));
        actions.andExpect(jsonPath("$.data.recentList[0].totalDistanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.recentList[0].totalDurationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.recentList[0].avgPace").value(500));
        actions.andExpect(jsonPath("$.data.recentList[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // badges[0]
        actions.andExpect(jsonPath("$.data.recentList[0].badges[0].id").value(1));
        actions.andExpect(jsonPath("$.data.recentList[0].badges[0].name").value("첫 시작"));
        actions.andExpect(jsonPath("$.data.recentList[0].badges[0].imageUrl").value("https://example.com/badges/first_run.png"));


        // page 정보
        actions.andExpect(jsonPath("$.data.page.totalCount").value(15));
        actions.andExpect(jsonPath("$.data.page.current").value(1));
        actions.andExpect(jsonPath("$.data.page.size").value(3));
        actions.andExpect(jsonPath("$.data.page.totalPage").value(5));
        actions.andExpect(jsonPath("$.data.page.isFirst").value(true));
        actions.andExpect(jsonPath("$.data.page.isLast").value(false));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_recent_pace_desc_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/recent?order=pace-desc")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.recentList[0].id").value(1));
        actions.andExpect(jsonPath("$.data.recentList[0].title").value("부산 서면역 15번 출구 100m 러닝"));
        actions.andExpect(jsonPath("$.data.recentList[0].totalDistanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.recentList[0].totalDurationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.recentList[0].avgPace").value(500));
        actions.andExpect(jsonPath("$.data.recentList[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // badges[0]
        actions.andExpect(jsonPath("$.data.recentList[0].badges[0].id").value(1));
        actions.andExpect(jsonPath("$.data.recentList[0].badges[0].name").value("첫 시작"));
        actions.andExpect(jsonPath("$.data.recentList[0].badges[0].imageUrl").value("https://example.com/badges/first_run.png"));

        // page 정보
        actions.andExpect(jsonPath("$.data.page.totalCount").value(15));
        actions.andExpect(jsonPath("$.data.page.current").value(1));
        actions.andExpect(jsonPath("$.data.page.size").value(3));
        actions.andExpect(jsonPath("$.data.page.totalPage").value(5));
        actions.andExpect(jsonPath("$.data.page.isFirst").value(true));
        actions.andExpect(jsonPath("$.data.page.isLast").value(false));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_recent_pace_asc_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/recent?order=pace-asc")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.recentList[0].id").value("16"));
        actions.andExpect(jsonPath("$.data.recentList[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data.recentList[0].totalDistanceMeters").value(1900));
        actions.andExpect(jsonPath("$.data.recentList[0].totalDurationSeconds").value(660));
        actions.andExpect(jsonPath("$.data.recentList[0].avgPace").value(347));
        actions.andExpect(jsonPath("$.data.recentList[0].createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.recentList[0].badges.length()").value(0));

        // page 정보
        actions.andExpect(jsonPath("$.data.page.totalCount").value(15));
        actions.andExpect(jsonPath("$.data.page.current").value(1));
        actions.andExpect(jsonPath("$.data.page.size").value(3));
        actions.andExpect(jsonPath("$.data.page.totalPage").value(5));
        actions.andExpect(jsonPath("$.data.page.isFirst").value(true));
        actions.andExpect(jsonPath("$.data.page.isLast").value(false));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_run_records_test() throws Exception {
        // given

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/runs")
                        .header("Authorization", "Bearer " + fakeToken)
        );

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.data[0].id").value(16));
        actions.andExpect(jsonPath("$.data[0].title").value("트랙 러닝 15"));
        actions.andExpect(jsonPath("$.data[0].createdAt").value(Matchers.matchesRegex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // 디버깅 및 문서화 (필요시 주석 해제)
        actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }
}

package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky.runrecord.RunRecordRequest;
import com.example.tracky.runrecord.enums.RunPlaceTypeEnum;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

        // 사진 생성
        // List<PictureRequest.DTO> pictures = new ArrayList<>();
        // PictureRequest.DTO picture1 = new PictureRequest.DTO();
        // picture1.setImgBase64("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD...
        // (아주 긴 이미지 데이터 문자열)");
        // picture1.setLat(35.1598);
        // picture1.setLon(129.1615);
        // picture1.setCreatedAt(Timestamp.valueOf("2025-06-22 06:35:15"));
        // pictures.add(picture1);

        // reqDTO.setPictures(pictures);

        String requestBody = om.writeValueAsString(reqDTO);

        log.debug("✅요청 바디: " + requestBody);

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .post("/s/api/runs")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then -> 결과를 코드로 검증 // json의 최상위 객체를 $ 표기한다
        actions.andExpect(status().isOk()); // HTTP 상태 코드는 isOk()로 검증하는 것이 일반적입니다.
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data 기본 필드
        actions.andExpect(jsonPath("$.data.id").value(15));
        actions.andExpect(jsonPath("$.data.title").value("부산 해운대 아침 달리기"));
        actions.andExpect(jsonPath("$.data.memo").doesNotExist());
        actions.andExpect(jsonPath("$.data.calories").value(200));
        actions.andExpect(jsonPath("$.data.totalDistanceMeters").value(2000));
        actions.andExpect(jsonPath("$.data.totalDurationSeconds").value(784));
        actions.andExpect(jsonPath("$.data.avgPace").value(392));
        actions.andExpect(jsonPath("$.data.bestPace").value(354));
        actions.andExpect(jsonPath("$.data.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.userId").value(1));

        // segments 첫 번째만
        actions.andExpect(jsonPath("$.data.segments[0].id").value(15));
        actions.andExpect(jsonPath("$.data.segments[0].startDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].endDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].durationSeconds").value(430));
        actions.andExpect(jsonPath("$.data.segments[0].distanceMeters").value(1000));
        actions.andExpect(jsonPath("$.data.segments[0].pace").value(430));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates.length()").value(3));

        // coordinates 첫 번째만
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lat").value(35.1587));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lon").value(129.1604));

        // badges 첫 번째만
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

        // pictures 빈 배열 확인
        actions.andExpect(jsonPath("$.data.pictures").isArray());
        actions.andExpect(jsonPath("$.data.pictures.length()").value(0));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_run_record_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/runs/{id}", id));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk()); // HTTP 상태 코드는 isOk()로 검증하는 것이 일반적입니다.
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data 기본 필드
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.title").value("부산 서면역 15번 출구 100m 러닝"));
        actions.andExpect(jsonPath("$.data.memo").value("서면역 15번 출구에서 NC백화점 방향으로 100m 직선 러닝"));
        actions.andExpect(jsonPath("$.data.calories").value(10));
        actions.andExpect(jsonPath("$.data.totalDistanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.totalDurationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.elapsedTimeInSeconds").value(50));
        actions.andExpect(jsonPath("$.data.avgPace").value(500)); // null 값 검증
        actions.andExpect(jsonPath("$.data.bestPace").value(500)); // null 값 검증
        actions.andExpect(jsonPath("$.data.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.userId").value(1));
        actions.andExpect(jsonPath("$.data.intensity").value(3));
        actions.andExpect(jsonPath("$.data.place").value("도로"));

        // segments 배열의 첫 번째 요소 검증
        actions.andExpect(jsonPath("$.data.segments[0].id").value(1));
        actions.andExpect(jsonPath("$.data.segments[0].startDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].endDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].durationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.segments[0].distanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.segments[0].pace").value(500)); // null 값 검증
        actions.andExpect(jsonPath("$.data.segments[0].coordinates.length()").value(26));

        // coordinates 배열 길이 검증
        // coordinates 배열의 첫 번째 요소 검증
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lat").value(35.1579));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lon").value(129.0594));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].recordedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // pictures 빈 배열 확인
        actions.andExpect(jsonPath("$.data.pictures").isArray());
        actions.andExpect(jsonPath("$.data.pictures.length()").value(0));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void delete_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/s/api/runs/{id}", id));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));
        actions.andExpect(jsonPath("$.data").value(nullValue())); // data 필드가 null인지 검증

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
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
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        // data 기본 필드
        actions.andExpect(jsonPath("$.data.id").value(1));
        actions.andExpect(jsonPath("$.data.title").value("수정 확인"));
        actions.andExpect(jsonPath("$.data.memo").value("수정 확인"));
        actions.andExpect(jsonPath("$.data.calories").value(10));
        actions.andExpect(jsonPath("$.data.totalDistanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.totalDurationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.elapsedTimeInSeconds").value(50));
        actions.andExpect(jsonPath("$.data.avgPace").value(500));
        actions.andExpect(jsonPath("$.data.bestPace").value(450));
        actions.andExpect(jsonPath("$.data.createdAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.userId").value(1));
        actions.andExpect(jsonPath("$.data.intensity").value(1));
        actions.andExpect(jsonPath("$.data.place").value("트랙"));

        // segments 배열의 첫 번째 요소 검증
        actions.andExpect(jsonPath("$.data.segments[0].id").value(1));
        actions.andExpect(jsonPath("$.data.segments[0].startDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].endDate").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));
        actions.andExpect(jsonPath("$.data.segments[0].durationSeconds").value(50));
        actions.andExpect(jsonPath("$.data.segments[0].distanceMeters").value(100));
        actions.andExpect(jsonPath("$.data.segments[0].pace").value(500));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates.length()").value(26));

        // coordinates 배열의 첫 번째 요소 검증
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lat").value(35.1579));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].lon").value(129.0594));
        actions.andExpect(jsonPath("$.data.segments[0].coordinates[0].recordedAt").value(Matchers.matchesPattern("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")));

        // pictures 빈 배열 확인
        actions.andExpect(jsonPath("$.data.pictures").isArray());
        actions.andExpect(jsonPath("$.data.pictures.length()").value(0));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void get_activities_week_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/week/test", id)
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(2));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(426));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(3000));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(1280));

        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("동메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/bronze.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value("2025-07-03 16:45:00"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].count").value(3));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].badgeType").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(nullValue()));

        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(3));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("두번째 러닝"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1000));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(400));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(300));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value("2025-07-04 15:00:00"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(11850));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(38150));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));
        actions.andExpect(jsonPath("$.data.runLevel.imageUrl").value("https://example.com/images/yellow.png"));

        actions.andExpect(jsonPath("$.data.weeks['2025-06'][0]").value(Matchers.matchesPattern("\\d{2}\\.\\d{2}~\\d{2}\\.\\d{2}")));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void get_activities_month_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/month", id)
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(2));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(426));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(3000));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(1280));

        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("동메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/bronze.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value("2025-07-03 16:45:00"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].count").value(3));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].badgeType").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(nullValue()));

        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(3));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("두번째 러닝"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1000));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(400));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(300));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value("2025-07-04 15:00:00"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(11850));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(38150));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));
        actions.andExpect(jsonPath("$.data.runLevel.imageUrl").value("https://example.com/images/yellow.png"));

        actions.andExpect(jsonPath("$.data.years[0]").value(2024));
        actions.andExpect(jsonPath("$.data.mounts['2024'][0]").value(2));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void get_activities_year_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/year", id)
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(12));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(439));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(20600));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(9060));

        actions.andExpect(jsonPath("$.data.totalStats.runCount").value(0.2));
        actions.andExpect(jsonPath("$.data.totalStats.avgPace").value(439));
        actions.andExpect(jsonPath("$.data.totalStats.avgDistanceMeters").value(858));
        actions.andExpect(jsonPath("$.data.totalStats.avgDurationSeconds").value(377));

        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("동메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/bronze.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value("2025-07-03 16:45:00"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].count").value(3));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].badgeType").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(nullValue()));

        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(3));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("두번째 러닝"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1000));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(400));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(300));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value("2025-07-04 15:00:00"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(11850));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(38150));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));
        actions.andExpect(jsonPath("$.data.runLevel.imageUrl").value("https://example.com/images/yellow.png"));

        actions.andExpect(jsonPath("$.data.years[0]").value(2024));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void get_activities_all_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/all", id)
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.avgStats.recodeCount").value(14));
        actions.andExpect(jsonPath("$.data.avgStats.avgPace").value(445));
        actions.andExpect(jsonPath("$.data.avgStats.totalDistanceMeters").value(11850));
        actions.andExpect(jsonPath("$.data.avgStats.totalDurationSeconds").value(5280));

        actions.andExpect(jsonPath("$.data.totalStats.runCount").value(0.1));
        actions.andExpect(jsonPath("$.data.totalStats.avgPace").value(445));
        actions.andExpect(jsonPath("$.data.totalStats.avgDistanceMeters").value(846));
        actions.andExpect(jsonPath("$.data.totalStats.avgDurationSeconds").value(377));

        actions.andExpect(jsonPath("$.data.achievementHistory[0].type").value("메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].name").value("동메달"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].imageUrl").value("https://example.com/rewards/bronze.png"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].achievedAt").value("2025-07-03 16:45:00"));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].count").value(3));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].badgeType").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordDistance").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordSeconds").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].runRecordPace").value(nullValue()));
        actions.andExpect(jsonPath("$.data.achievementHistory[0].isAchieved").value(nullValue()));

        actions.andExpect(jsonPath("$.data.recentRuns[0].id").value(3));
        actions.andExpect(jsonPath("$.data.recentRuns[0].title").value("두번째 러닝"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDistanceMeters").value(1000));
        actions.andExpect(jsonPath("$.data.recentRuns[0].totalDurationSeconds").value(400));
        actions.andExpect(jsonPath("$.data.recentRuns[0].avgPace").value(300));
        actions.andExpect(jsonPath("$.data.recentRuns[0].createdAt").value("2025-07-04 15:00:00"));
        actions.andExpect(jsonPath("$.data.recentRuns[0].badges.length()").value(0));

        actions.andExpect(jsonPath("$.data.runLevel.totalDistance").value(11850));
        actions.andExpect(jsonPath("$.data.runLevel.distanceToNextLevel").value(38150));
        actions.andExpect(jsonPath("$.data.runLevel.name").value("옐로우"));
        actions.andExpect(jsonPath("$.data.runLevel.imageUrl").value("https://example.com/images/yellow.png"));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void get_activities_recent_test() throws Exception {
        // given
        Integer id = 1;

        // when
        ResultActions actions = mvc.perform(
                MockMvcRequestBuilders
                        .get("/s/api/activities/recent", id)
                        .contentType(MediaType.APPLICATION_JSON));

        // eye
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        log.debug("✅응답 바디: " + responseBody);

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.status").value(200));
        actions.andExpect(jsonPath("$.msg").value("성공"));

        actions.andExpect(jsonPath("$.data.groupedrecentList[0].yearMonth").value("2025-07-01 00:00:00"));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].avgStats.recodeCount").value(2));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].avgStats.avgPace").value(426));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].avgStats.totalDistanceMeters").value(1500));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].avgStats.totalDurationSeconds").value(640));

        actions.andExpect(jsonPath("$.data.groupedrecentList[0].recentRuns[0].id").value(3));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].recentRuns[0].title").value("두번째 러닝"));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].recentRuns[0].totalDistanceMeters").value(1000));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].recentRuns[0].totalDurationSeconds").value(400));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].recentRuns[0].avgPace").value(300));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].recentRuns[0].createdAt").value("2025-07-04 15:00:00"));
        actions.andExpect(jsonPath("$.data.groupedrecentList[0].recentRuns[0].badges.length()").value(0));

        actions.andExpect(jsonPath("$.data.page.totalCount").value(5));
        actions.andExpect(jsonPath("$.data.page.current").value(1));
        actions.andExpect(jsonPath("$.data.page.size").value(3));
        actions.andExpect(jsonPath("$.data.page.totalPage").value(2));
        actions.andExpect(jsonPath("$.data.page.isFirst").value(true));
        actions.andExpect(jsonPath("$.data.page.isLast").value(false));

        // 디버깅 및 문서화 (필요시 주석 해제)
        // actions.andDo(MockMvcResultHandlers.print());
    }

}

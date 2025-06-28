package com.example.tracky.integration;

import com.example.tracky.MyRestDoc;
import com.example.tracky.runrecord.Enum.RunPlaceEnum;
import com.example.tracky.runrecord.RunRecordRequest;
import com.example.tracky.runrecord.runsegment.RunSegmentRequest;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

// 컨트롤러 통합 테스트
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // MOCK -> 가짜 환경을 만들어 필요한 의존관계를 다 메모리에 올려서 테스트
@Slf4j
public class RunRecordControllerTest extends MyRestDoc {

    @Autowired
    private ObjectMapper om; // json <-> java Object 변환 해주는 객체. IoC에 objectMapper가 이미 떠있음

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
        segment1.setStartDate(Timestamp.valueOf("2025-06-22 06:30:00"));
        segment1.setEndDate(Timestamp.valueOf("2025-06-22 06:37:10"));
        segment1.setDurationSeconds(430);
        segment1.setDistanceMeters(1000);

        List<RunCoordinateRequest.DTO> coordinates1 = new ArrayList<>();
        RunCoordinateRequest.DTO coord1 = new RunCoordinateRequest.DTO();
        coord1.setLat(35.1587);
        coord1.setLon(129.1604);
        coord1.setCreatedAt(Timestamp.valueOf("2025-06-22 06:30:00"));
        coordinates1.add(coord1);

        RunCoordinateRequest.DTO coord2 = new RunCoordinateRequest.DTO();
        coord2.setLat(35.1595);
        coord2.setLon(129.1612);
        coord2.setCreatedAt(Timestamp.valueOf("2025-06-22 06:33:45"));
        coordinates1.add(coord2);

        RunCoordinateRequest.DTO coord3 = new RunCoordinateRequest.DTO();
        coord3.setLat(35.1602);
        coord3.setLon(129.1620);
        coord3.setCreatedAt(Timestamp.valueOf("2025-06-22 06:37:10"));
        coordinates1.add(coord3);

        segment1.setCoordinates(coordinates1);

        segments.add(segment1);

        RunSegmentRequest.DTO segment2 = new RunSegmentRequest.DTO();
        segment2.setStartDate(Timestamp.valueOf("2025-06-22 06:37:11"));
        segment2.setEndDate(Timestamp.valueOf("2025-06-22 06:43:05"));
        segment2.setDurationSeconds(354);
        segment2.setDistanceMeters(1000);

        List<RunCoordinateRequest.DTO> coordinates2 = new java.util.ArrayList<>();
        RunCoordinateRequest.DTO coord4 = new RunCoordinateRequest.DTO();
        coord4.setLat(35.1610);
        coord4.setLon(129.1628);
        coord4.setCreatedAt(Timestamp.valueOf("2025-06-22 06:40:00"));
        coordinates2.add(coord4);

        RunCoordinateRequest.DTO coord5 = new RunCoordinateRequest.DTO();
        coord5.setLat(35.1618);
        coord5.setLon(129.1635);
        coord5.setCreatedAt(Timestamp.valueOf("2025-06-22 06:43:05"));
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
        // actions.andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400));
        // actions.andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("중복된 유저네임이
        // 존재합니다"));
        // actions.andExpect(MockMvcResultMatchers.jsonPath("$.body").value(Matchers.nullValue()));
        // actions.andDo(MockMvcResultHandlers.print()).andDo(document);
    }

    @Test
    public void getRunRecord_test() throws Exception {
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
    }

    @Test
    public void update_test() throws Exception {
        // given
        Integer id = 1;
        RunRecordRequest.UpdateDTO reqDTO = new RunRecordRequest.UpdateDTO();
        reqDTO.setTitle("수정 확인");
        reqDTO.setMemo("수정 확인");
        reqDTO.setPlace(RunPlaceEnum.TRACK);
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
    }
    
}

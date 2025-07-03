package com.example.tracky._core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * JSON <-> DTO 변환을 위한 유틸리티 클래스
 * - LocalDateTime은 "yyyy-MM-dd HH:mm:ss" 포맷을 사용
 */
public class JsonUtil {

    // yyyy-MM-dd HH:mm:ss 형식의 날짜 문자열을 파싱/포맷팅하기 위한 DateTimeFormatter
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ObjectMapper는 Jackson의 JSON 변환 핵심 객체
    // - 무겁고 스레드 세이프하므로 static으로 재사용
    // - JavaTimeModule로 LocalDateTime 등 Java 8 날짜 타입 지원
    // - addDeserializer로 LocalDateTime을 "yyyy-MM-dd HH:mm:ss" 포맷으로 파싱하도록 커스텀 등록
    // - WRITE_DATES_AS_TIMESTAMPS 비활성화로 날짜를 숫자(타임스탬프)가 아닌 문자열로 처리
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATETIME_FORMATTER)))
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * DTO 리스트를 JSON 문자열로 변환
     */
    public static <T> String toJson(List<T> dtoList) {
        try {
            return objectMapper.writeValueAsString(dtoList);
        } catch (Exception e) {
            throw new RuntimeException("리스트를 JSON 문자열로 변환 실패", e);
        }
    }

    /**
     * JSON 문자열을 DTO 리스트로 변환
     */
    public static <T> List<T> fromJson(String json, TypeReference<List<T>> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("JSON 문자열을 리스트로 변환 실패", e);
        }
    }
}

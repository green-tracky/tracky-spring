package com.example.tracky._core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <pre>
 * JSON <-> DTO 변환을 위한 유틸리티 클래스
 * - LocalDateTime은 "yyyy-MM-dd HH:mm:ss" 포맷을 사용
 * TODO : 예외처리 해야함
 * </pre>
 */
public class JsonUtil {

    // yyyy-MM-dd HH:mm:ss 형식의 날짜 문자열을 파싱/포맷팅하기 위한 DateTimeFormatter
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ObjectMapper는 Jackson의 JSON 변환 핵심 객체
    // - 무겁고 스레드 세이프하므로 static으로 재사용한다 (성능 최적화)
    // - JavaTimeModule을 등록하면 Java 8 날짜 타입(LocalDateTime 등)을 지원하게 된다
    // - addDeserializer: 문자열 → LocalDateTime 변환 시 사용할 포맷 지정 ("yyyy-MM-dd HH:mm:ss")
    // - addSerializer: LocalDateTime → 문자열 변환 시 사용할 포맷 지정 ("yyyy-MM-dd HH:mm:ss") ✅ 필수!
    // - WRITE_DATES_AS_TIMESTAMPS 비활성화: 날짜를 숫자(timestamp) 대신 문자열로 출력하게 한다
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATETIME_FORMATTER))
                    .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATETIME_FORMATTER))) // ✅ 직렬화 포맷 지정
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /**
     * <pre>
     * DTO 리스트를 JSON 문자열로 변환
     * 좌표 배열을 JSON 문자열로 변환
     * </pre>
     */
    public static <T> String toJson(List<T> dtoList) {
        try {
            return objectMapper.writeValueAsString(dtoList);
        } catch (Exception e) {
            throw new RuntimeException("리스트를 JSON 문자열로 변환 실패", e);
        }
    }

    /**
     * <pre>
     * 좌표 JSON 문자열을 DTO 로 변환
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

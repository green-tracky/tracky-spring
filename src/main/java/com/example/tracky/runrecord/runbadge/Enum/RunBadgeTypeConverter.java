package com.example.tracky.runrecord.runbadge.Enum;

import com.example.tracky._core.error.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi500;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

/**
 * JPA의 AttributeConverter를 구현하여, Java의 RunBadgeType Enum과 데이터베이스의 한글 String 값을
 * 자동으로 상호 변환하는 클래스입니다.
 *
 * @Converter(autoApply = true): 이 어노테이션 덕분에 RunBadgeType을 사용하는 모든 엔티티 필드에
 * 이 컨버터가 자동으로 적용되어, 개발자가 매번 @Convert를 명시할 필요가 없습니다.
 */
@Converter(autoApply = true)
public class RunBadgeTypeConverter implements AttributeConverter<RunBadgeType, String> {

    /**
     * [Java Enum -> DB String]
     * Java 코드의 Enum 상수를 데이터베이스에 저장될 한글 문자열로 변환합니다.
     * 예: RunBadgeType.RECORD -> "최고 기록"
     *
     * @param runBadgeType 엔티티에 저장된 RunBadgeType Enum 객체
     * @return 데이터베이스에 저장될 한글 문자열
     */
    @Override
    public String convertToDatabaseColumn(RunBadgeType runBadgeType) {
        // null 값이 들어올 경우를 대비한 방어 코드입니다.
        if (runBadgeType == null) {
            return null;
        }
        // Enum이 가진 한글 값(value 필드)을 반환합니다.
        return runBadgeType.getValue();
    }

    /**
     * [DB String -> Java Enum]
     * 데이터베이스에서 읽어온 한글 문자열을 Java 코드의 Enum 상수로 변환합니다.
     * 예: "최고 기록" -> RunBadgeType.RECORD
     *
     * @param dbData 데이터베이스에서 읽어온 한글 문자열 (예: "최고 기록", "월간 업적")
     * @return 문자열에 해당하는 RunBadgeType Enum 객체
     */
    @Override
    public RunBadgeType convertToEntityAttribute(String dbData) {
        // null 값이 들어올 경우를 대비한 방어 코드입니다.
        if (dbData == null) {
            return null;
        }

        // Enum의 모든 상수를 스트림으로 변환하여 순회합니다.
        return Stream.of(RunBadgeType.values())
                // 각 Enum 상수가 가진 한글 값(value)이 DB에서 읽어온 문자열(dbData)과 일치하는지 확인합니다.
                .filter(c -> c.getValue().equals(dbData))
                // 일치하는 첫 번째 Enum 상수를 찾습니다.
                .findFirst()
                // 만약 일치하는 상수가 없다면 (DB에 잘못된 값이 들어갔다면),
                // IllegalArgumentException을 발생시켜 데이터 불일치 문제를 조기에 발견하도록 합니다.
                .orElseThrow(() -> new ExceptionApi500(ErrorCodeEnum.INVALID_DATABASE_DATA));
    }
}

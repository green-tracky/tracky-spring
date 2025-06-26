package com.example.tracky.runrecord.runbadge.Enum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

/**
 * RunBadgeType Enum을 데이터베이스의 한글 값('업적', '기록')으로 변환하는 컨버터입니다.
 *
 * @Converter(autoApply = true): 이 컨버터를 RunBadgeType을 사용하는 모든 엔티티 필드에 자동으로 적용합니다.
 * 이를 통해 엔티티에서는 @Enumerated 어노테이션 없이도 자동 변환이 이루어집니다.
 */
@Converter(autoApply = true)
public class RunBadgeTypeConverter implements AttributeConverter<RunBadgeType, String> {

    /**
     * Enum(RunBadgeType)을 DB에 저장될 한글 값으로 변환합니다.
     * 예: RunBadgeType.ACHIEVEMENT -> "업적"
     */
    @Override
    public String convertToDatabaseColumn(RunBadgeType runBadgeType) {
        if (runBadgeType == null) {
            return null;
        }
        return runBadgeType.getValue();
    }

    /**
     * DB에서 조회한 한글 값을 Enum(RunBadgeType)으로 변환합니다.
     * 예: "업적" -> RunBadgeType.ACHIEVEMENT
     */
    @Override
    public RunBadgeType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(RunBadgeType.values())
                .filter(c -> c.getValue().equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(dbData + "에 해당하는 Enum 상수가 없습니다."));
    }
}

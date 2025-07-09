package com.example.tracky._core.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA의 AttributeConverter를 구현하여, Java의 RunBadgeType Enum과 데이터베이스의 한글 String 값을
 * 자동으로 상호 변환하는 클래스입니다.
 *
 * @Converter(autoApply = true): 이 어노테이션 덕분에 RunBadgeType을 사용하는 모든 엔티티 필드에
 * 이 컨버터가 자동으로 적용되어, 개발자가 매번 @Convert를 명시할 필요가 없습니다.
 */
@Converter(autoApply = true)
public class RunBadgeTypeConverter implements AttributeConverter<RunBadgeTypeEnum, String> {

    @Override
    public String convertToDatabaseColumn(RunBadgeTypeEnum attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public RunBadgeTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        return RunBadgeTypeEnum.fromValue(dbData);
    }
}
